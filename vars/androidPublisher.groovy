def androidPublisher(String projectName, Boolean isUnitTestOnly) {

    def unitTestBaseDirectory = projectName + '/build/reports/tests/'
    def instrumentedBaseDirectory = projectName + '/build/reports/androidTests/connected'
    def releaseUnitTest = unitTestBaseDirectory + 'testReleaseUnitTest'
    def debugUnitTest = unitTestBaseDirectory + 'testDebugUnitTest'

    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
    reportDir: debugUnitTest, reportFiles: 'index.html', reportName: 'Debug Report', reportTitles: ''])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
    reportDir: releaseUnitTest,  reportFiles: 'index.html', reportName: 'Release Report', reportTitles: ''])

    if (!isUnitTestOnly) {
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
      reportDir: instrumentedBaseDirectory, reportFiles: 'index.html', reportName: 'Instrumented Report', reportTitles: ''])
    }
}
