node {
 stage "checkout"
 checkout scm

 stage "build"
 sh "./gradlew clean build"

 stage "spoon"
 lock('adb') {
  sh "./gradlew spoonWithAnalyticsWithCloudDebugAndroidTest"
 }
}