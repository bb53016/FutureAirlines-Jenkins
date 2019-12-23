
def gitCreds           = '<GitHub Credentail encoded into Jenkins>'
def gitBuildRepo       = '<URL to GitHub Repository>' 

pipelineJob("Developer-CI-Pipeline") {
  description('')
  [$class: 'BuildBlockerProperty',
     blockLevel: 'GLOBAL',
     blockingJobs: 'Deployment-Pipeline',
     scanQueueFor: 'buildable',
     useBuildBlocker: true
  ]
  parameters {
    choiceParam('gitCreds', [gitCreds], '')
    choiceParam('gitUrl', [gitBuildRepo], '')
    stringParam('terraformBucket', "s3-bucket-name", 'Terraform bucket for tfstate file')
    stringParam('terraformPrefix', "s3-bucket-directory", 'Terraform directory for tfstate file')
    stringParam('terraformKey', "s3-bucket-filename", 'Terraform tfstate file name')
  }
  definition {
    cps {
      script(readFileFromWorkspace('pipelines/pipelines.groovy'))
      sandbox()
    }
  }
  logRotator(30,100)
}
