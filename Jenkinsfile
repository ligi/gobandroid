node {
 def flavorCombination='WithAnalyticsWithCloud'
 def flavorCombinationLower='withAnalyticsWithCloud'

 stage 'checkout'
 checkout scm

 stage 'assemble'
 sh "./gradlew clean assemble${flavorCombination}Release"
 archive 'android/build/outputs/apk/*'

 stage 'lint'
 sh "./gradlew clean lint${flavorCombination}Release"
publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/outputs/', reportFiles: "lint-results-*Release.html", reportName: 'Lint'])

 stage 'test'
 sh "./gradlew clean test${flavorCombination}DebugUnitTest"
 publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])

 stage 'UITest'
 lock('adb') {
   try {
    sh "./gradlew clean spoon${flavorCombination}"
   } catch(err) {
    currentBuild.result = FAILURE
   } finally {
     publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "android/build/spoon/${flavorCombinationLower}/debug", reportFiles: 'index.html', reportName: 'Spoon'])
   }
 }
}