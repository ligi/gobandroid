node {
 stage "checkout"
 checkout scm

 stage "build"
 sh "./gradlew clean build"

 stage "test"
 sh "./gradlew spoonWithAnalyticsWithCloudDebugAndroidTest"
}