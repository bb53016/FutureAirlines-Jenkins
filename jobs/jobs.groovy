
def gitCreds           = '<GitHub Credentail encoded into Jenkins>'
def build-gitBuildRepo       = '<URL to GitHub Repository>' 
def test-gitBuildRepo       = '<URL to GitHub Repository>' 

pipelineJob("Build Job") {
  description('')
  [$class: 'BuildBlockerProperty',
     blockLevel: 'GLOBAL',
     blockingJobs: 'Build Job',
     scanQueueFor: 'buildable',
     useBuildBlocker: true
  ]
  parameters {
    choiceParam('gitCreds', [gitCreds], '')
    choiceParam('gitUrl', [build-gitBuildRepo], '')
  }
  definition {
    cps {
      script(readFileFromWorkspace('pipelines/build.groovy'))
      sandbox()
    }
  }
  logRotator(30,100)
}


pipelineJob("Test Job") {
  description('')
  [$class: 'BuildBlockerProperty',
     blockLevel: 'GLOBAL',
     blockingJobs: 'Test Job',
     scanQueueFor: 'buildable',
     useBuildBlocker: true
  ]
  parameters {
    choiceParam('gitCreds', [gitCreds], '')
    choiceParam('gitUrl', [test-gitBuildRepo], '')
    stringParam('terraformBucket', "s3-bucket-name", 'Terraform bucket for tfstate file')
    stringParam('terraformPrefix', "s3-bucket-directory", 'Terraform directory for tfstate file')
    stringParam('terraformKey', "s3-bucket-filename", 'Terraform tfstate file name')
    choiceParam('terraformApplyPlan', ['True', 'False'], '')
    
  }
  definition {
    cps {
      script(readFileFromWorkspace('pipelines/test.groovy'))
      sandbox()
    }
  }
  logRotator(30,100)
}
