def call(Map config) {

  pipeline {
    def gitUri = config.gitUri
    def moduleName = config.moduleName
    def credentialsId = config.credentialsId
    def useAvd = config.containsKey('useAvd') ? config.useAvd : true

    if (gitUri == null || moduleName == null || credentialsId == null) {
        throw new IllegalStateException('Missing configuration arguments')
    }

    androidSmokeTest(config)

    stage ('Sonar') {
      withSonarQubeEnv {
        sh "./gradlew :${moduleName}:sonarqube"
      }
    }
  }
}
