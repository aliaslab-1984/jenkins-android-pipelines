def call(Map config) {

  pipeline {
    def gitUri = config.gitUri
    def moduleName = config.moduleName
    def credentialsId = config.credentialsId
    def gitBranch = config.gitBranch ?: 'master'
    def lintReportPattern = config.lintReportPattern ?: '**/lint-results.xml'

    if (gitUri == null || moduleName == null || credentialsId == null) {
        throw new IllegalStateException('Missing configuration arguments')
    }

    stage ('Checkout') {
        git credentialsId: credentialsId, url: gitUri, branch: gitBranch
    }

    stage ('Clean') {
        sh "./gradlew :${moduleName}:clean"
    }

  	//stage ('Analyze') {
  	//    sh "./gradlew :${moduleName}:lint"
  	//    recordIssues tool: androidLintParser(pattern: lintReportPattern)
  	//}

  	stage ('Build') {
  	   sh "./gradlew :${moduleName}:clean :${moduleName}:assemble"
  	}
  }
}
