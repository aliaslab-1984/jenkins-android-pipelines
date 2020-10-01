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

    stage ('Instrumented Test') {
      def instrumentedTestCommand = "./gradlew :${moduleName}:createDebugCoverageReport"
        if (useAvd) {
            withEnv(['JAVA_OPTS=-XX:+IgnoreUnrecognizedVMOptions']) {
                withAvd(hardwareProfile: 'pixel', systemImage: 'system-images;android-28;default;x86_64', abi: 'x86_64') {
                    sh instrumentedTestCommand
                }
            }
        } else {
            sh instrumentedTestCommand
        }
    }

    stage ('Sonar') {
      withSonarQubeEnv {
        sh "./gradlew :${moduleName}:sonarqube"
      }
    }
  }
}
