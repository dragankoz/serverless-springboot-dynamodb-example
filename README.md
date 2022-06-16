# Serverless Spring Boot Native+JVM Application Example

## Required Installations
- AWS CLI 64-bit (Windows)
  
  https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html



- AWS SAM CLI 64-bit (Windows)

  https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install-windows.html


- WSL2 + GraalVM 11

  https://www.graalvm.org/21.3/docs/getting-started/linux/
  
  https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.1.0/graalvm-ce-java11-linux-amd64-22.1.0.tar.gz

## Prerequisites
- Turn off Windows Path inclusion on WSL, add [interop] to the wsl.conf
```
C:\> wsl
# vi /etc/wsl.conf
[interop]
enabled=false # enable launch of Windows binaries; default is true
appendWindowsPath=false # append Windows path to $PATH variable; default is true

C:\> wsl --shutdown
```

- Install GraalVM Java 11 under WSL2
```
C:\> wsl
$ sudo -s
# cd /tmp
# wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.1.0/graalvm-ce-java11-linux-amd64-22.1.0.tar.gz
# tar -xvf graalvm-ce-java11-linux-amd64-22.1.0.tar.gz
# exit
# mv graalvm-ce-java11-22.1.0 /opt
$ echo 'export JAVA_HOME=/opt/graalvm-ce-java11-22.1.0' | tee -a ~/.bashrc
$ echo 'export PATH=$PATH:$JAVA_HOME/bin' |tee -a ~/.bashrc
$ source ~/.bashrc
$ java -version
OpenJDK Runtime Environment GraalVM CE 22.1.0... 
OpenJDK 64-Bit Server VM GraalVM CE 22.1.0...
```

### Build Native
```
C:\> wsl
$ ./build/build-native-wsl.sh
```

### Deploy Native
```
C:\> cd deploy\native
C:\> deploy-aws.bat

Configuring SAM deploy
======================

        Looking for config file [samconfig.toml] :  Not found

        #Shows you resources changes to be deployed and require a 'Y' to initiate deploy
        Confirm changes before deploy [y/N]:
        #SAM needs permission to be able to create roles to connect to the resources in your template
        Allow SAM CLI IAM role creation [Y/n]:
        #Preserves the state of previously provisioned resources when an operation fails
        Disable rollback [Y/n]:
        GetProductByIdFunction may not have authorization defined, Is this okay? [y/N]: y
        GetProductsFunction may not have authorization defined, Is this okay? [y/N]: y
        CreateUpdateProductFunction may not have authorization defined, Is this okay? [y/N]: y
        DeleteProductFunction may not have authorization defined, Is this okay? [y/N]: y
        Save arguments to configuration file [Y/n]:
        SAM configuration file [samconfig.toml]:
        SAM configuration environment [default]:

        Looking for resources needed for deployment:
        Creating the required resources...
        Successfully created!
         Managed S3 bucket: aws-sam-cli-managed-default-samclisourcebucket-14rlqev6rtdwa
         A different default S3 bucket can be set in samconfig.toml                     

        Saved arguments to config file
        Running 'sam deploy' for future deployments will use the parameters saved above.
        The above parameters can be changed by modifying samconfig.toml
        Learn more about samconfig.toml syntax at
        https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-config.html

```

### Build JVM
```
C:\> mvnw clean integration-test -Pjvm
```

### Deploy JVM
```
C:\> cd deploy\jvm
C:\> deploy-aws.bat

Configuring SAM deploy
======================

        Looking for config file [samconfig.toml] :  Not found

        Setting default arguments for 'sam deploy'
        =========================================
        Stack Name [springboot-example-jvm]:
        AWS Region [ap-southeast-2]:  
        #Shows you resources changes to be deployed and require a 'Y' to initiate deploy
        Confirm changes before deploy [y/N]: 
        #SAM needs permission to be able to create roles to connect to the resources in your template
        Allow SAM CLI IAM role creation [Y/n]:
        #Preserves the state of previously provisioned resources when an operation fails
        Disable rollback [Y/n]:
        GetProductByIdFunction may not have authorization defined, Is this okay? [y/N]: y
        GetProductsFunction may not have authorization defined, Is this okay? [y/N]: y
        CreateUpdateProductFunction may not have authorization defined, Is this okay? [y/N]: y
        DeleteProductFunction may not have authorization defined, Is this okay? [y/N]: y
        Save arguments to configuration file [Y/n]: 
        SAM configuration file [samconfig.toml]: 
        SAM configuration environment [default]: 

        Looking for resources needed for deployment:
         Managed S3 bucket: aws-sam-cli-managed-default-samclisourcebucket-14rlqev6rtdwa
         A different default S3 bucket can be set in samconfig.toml

        Saved arguments to config file
        Running 'sam deploy' for future deployments will use the parameters saved above.
        The above parameters can be changed by modifying samconfig.toml
        Learn more about samconfig.toml syntax at
        https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-config.html

```