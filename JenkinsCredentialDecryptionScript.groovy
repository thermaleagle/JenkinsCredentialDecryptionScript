// Run this by pasting the content of this file within your Jenkins Script Console which you can do only if you are a Jenkins admin.
// Replace "your-credential-id" with the ID of the credential you want to decrypt. This ID must be present on the Jenkins instance where you are executing this script.

import jenkins.model.Jenkins
import hudson.util.Secret
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import org.jenkinsci.plugins.plaincredentials.FileCredentials

// Replace with your credential ID
def credentialId = "your-credential-id"

// Get the Jenkins instance
def jenkinsInstance = Jenkins.getInstanceOrNull()

// Get the credentials store
def domain = Domain.global()
def credentialsStore = jenkinsInstance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// Function to convert InputStream to String
def convertStreamToString(InputStream is) {
    def s = new Scanner(is).useDelimiter("\\A")
    return s.hasNext() ? s.next() : ""
}

// Find the credential by ID
def credential = credentialsStore.getCredentials(domain).find { it.id == credentialId }

if (credential instanceof com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials) {
    // Decrypt the username and password
    def username = credential.getUsername()
    def password = Secret.toString(credential.getPassword())
    println "Decrypted username: ${username}"
    println "Decrypted password: ${password}"
} else if (credential instanceof org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl) {
    // Decrypt the secret text
    def secretText = Secret.toString(credential.getSecret())
    println "Decrypted secret: ${secretText}"
} else if (credential instanceof org.jenkinsci.plugins.plaincredentials.FileCredentials) {
    // Decrypt the secret file
    def fileName = credential.getFileName()
    def fileContent = convertStreamToString(credential.getContent())
    println "Decrypted file name: ${fileName}"
    println "Decrypted file content: ${fileContent}"
} else if (credential instanceof com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey) {
    // Decrypt the SSH private key
    def username = credential.getUsername()
    def privateKeys = credential.getPrivateKeys()
    println "Decrypted SSH username: ${username}"
    privateKeys.eachWithIndex { key, index ->
        println "Decrypted SSH private key ${index + 1}: ${key}"
    }
} else {
    println "Credential not found or not of a recognized type"
}
