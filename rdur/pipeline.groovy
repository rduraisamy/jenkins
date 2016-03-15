// Standard Github copyright
// We can change the labels for the nodes as needed

node('master') {
  // Mark the code checkout 'stage'....
  stage 'Checkout'

  // Get some code from a GitHub repository
  // Note that there are many more parameters that could be used here
  git url: 'https://github.com/rduraisamy/jenkins.git'

  // Get the maven tool.
  def mvnHome = tool 'M3'

  // Mark the code build 'stage'....
  stage 'Build'

  // Run the maven build
  sh "${mvnHome}/bin/mvn install -pl war -am -DskipTests"
 
  // Run the maven tests 
  stage 'Test'
  sh "${mvnHome}/bin/mvn -Plight-test install"
 
  // Archive the artifacts that will be used for deployment
  archive 'war/target/jenkins.war'
}

node ('ubuntu-server') {
   stage 'Deploy'

   // Load the artifacts that we are going to deploy
   unarchive mapping: ['war/target/jenkins.war' : '/home/rdur/jenkins.war']

   // remove the current production jenkins install
   sh "rm -f mypid"
   sh "ps -efwww  |grep -v grep|grep  \"/home/rdur/jenkins.war\"|awk {'print \$2'}>mypid"
 
   sh "if [ -s mypid ]; then kill -9  `cat mypid`; fi"
   sh "sleep 10"

   // install the latest jenkins   
   sh "sudo nohup java -jar /home/rdur/jenkins.war &"
   sh "ls"

}




