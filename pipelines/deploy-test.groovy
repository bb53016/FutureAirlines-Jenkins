

node ('master') {
  writeFile(file: "git-askpass-${BUILD_TAG}", text: "#!/bin/bash\ncase \"\$1\" in\nUsername*) echo \"\${STASH_USERNAME}\" ;;\nPassword*) echo \"\${STASH_PASSWORD}\" ;;\nesac")
  sh "chmod a+x git-askpass-${BUILD_TAG}"

  dir(env) {

    stage ('GitHub Checkout') {
      git_checkout()
       }   
	  
   //Build EC2 instance using terraform to test code
  stage('start test ec2 instance') {

       stage ('Compute Stack Remote State') {
	 sh "terraform init -no-color -force-copy -input=false -upgrade=true'"
       }

       stage ('Compute Stack Plan') {
         env_tfvars = "../../infras-as-code/terraform/ec2/config.tfvars"
	 sh "terraform plan -no-color -out=tfplan -input=false -var-file=${env_tfvars}"
       }

       if (terraformApplyPlan == 'true') {
         stage ('Compute Stack Apply') {
           sh "terraform apply -input=false -no-color tfplan"
         }
       }
	  
    stage('Test Code') {
        steps {
           echo 'Testing Code...'
            }
        }
     }

  }
}

def git_checkout() {
	checkout([$class: 'GitSCM', branches: [[name: gitBranch]], clearWorkspace: true, doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCreds, url: gitUrl]]])
}
