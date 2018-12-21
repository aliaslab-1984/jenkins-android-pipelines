def call(Map config) {

  pipeline {
    def gitUri = config.gitUri
    def moduleName = config.moduleName
    def credentialsId = config.credentialsId

    if (gitUri == null || moduleName == null || credentialsId == null) {
        throw new IllegalStateException('Missing configuration arguments')
    }

    stage ('Checkout') {
      checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: gitUri]]])
    }

    stage ('Analyze') {
      sh "./gradlew :${moduleName}:lint"
      androidLint()
    }

    stage ('Build') {
      sh "./gradlew :${moduleName}:clean :${moduleName}:assemble"
    }

    stage ('Unit Test') {
      sh "./gradlew :${moduleName}:jacocoTestReport"
    }

    stage ('Instrumented Test') {
      withEnv(['JAVA_OPTS=-XX:+IgnoreUnrecognizedVMOptions --add-modules java.se.ee']) {
        withAvd(hardwareProfile: 'pixel', systemImage: 'system-images;android-24;default;x86_64', abi: 'x86_64') {
          sh "./gradlew :${moduleName}:createDebugCoverageReport"
        }
      }
    }

    stage ('Sonar') {
      withSonarQubeEnv {
        sh "./gradlew :${moduleName}:sonarqube"
      }
    }
  }
}
