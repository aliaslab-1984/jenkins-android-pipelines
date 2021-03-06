def call(Map config) {

  pipeline {
    def gitUri = config.gitUri
    def moduleName = config.moduleName
    def credentialsId = config.credentialsId
    def gitBranch = config.gitBranch ?: 'master'

    if (gitUri == null || moduleName == null || credentialsId == null) {
        throw new IllegalStateException('Missing configuration arguments')
    }

    stage ('Checkout') {
        git credentialsId: credentialsId, url: gitUri, branch: gitBranch
    }

  	stage ('Build') {
  	   sh "./gradlew :${moduleName}:clean :${moduleName}:assemble"
  	}

  	stage ('Deploy') {
  	    sh "./gradlew :${moduleName}:artifactoryPublish"
  	}
  }
}
