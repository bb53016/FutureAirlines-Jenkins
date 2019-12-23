

node ('deployer') {

  dir(env) {

    stage ('GitHub Checkout') {
      git_checkout()
	    
    stage('Test Code') {
        steps {
           echo 'Testing Code...'
            }
        }    
	    
   //Build EC2 instance using terraform to test code

    dir(terraformdir_compute) {

       stage ('Compute Stack Remote State') {
         terraformKey = "app_compute.tfstate"
         terraform_init(terraformBucket, terraformPrefix, terraformKey)
       }

       stage ('Compute Stack Plan') {
         env_tfvars = "../../infras-as-code/terraform/ec2/config.tfvars"
         terraform_plan(terraformenv, env_tfvars)
       }

       if (terraformApplyPlan == 'true') {
         stage ('Compute Stack Apply') {
           terraform_apply()
         }
       }

     }

  }
}

def git_checkout() {
	checkout([$class: 'GitSCM', branches: [[name: gitBranch]], clearWorkspace: true, doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCreds, url: gitUrl]]])
}

def terraform_init(terraformBucket,terraformPrefix,terraformkey) {
	withEnv(["GIT_ASKPASS=${WORKSPACE}/git-askpass-${BUILD_TAG}"]) {
		withCredentials([usernamePassword(credentialsId: gitCreds, passwordVariable: 'STASH_PASSWORD', usernameVariable: 'STASH_USERNAME')]) {
		sh "terraform init -no-color -force-copy -input=false -upgrade=true -backend=true -backend-config='bucket=${terraformBucket}' -backend-config='workspace_key_prefix=${terraformPrefix}' -backend-config='key=${terraformkey}'"
		sh "terraform get -no-color -update=true"
		}
	}
}

def terraform_plan(workspace,env_tfvars) {
	sh "terraform workspace new ${workspace}"
	sh "terraform workspace select ${workspace}"
    {
	sh "terraform plan -no-color -out=tfplan -input=false -var-file=${env_tfvars}"
    }
}

def terraform_apply() {
	sh "terraform apply -input=false -no-color tfplan"
}
