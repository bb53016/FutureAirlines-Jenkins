

node ('deployer') {

  dir(env) {

    stage ('GitHub Checkout') {
      git_checkout()
      }
	  
    stage('Build Code') {
       steps {
          echo 'Building and compiling code...'
            }
        }
	   

  }
}
