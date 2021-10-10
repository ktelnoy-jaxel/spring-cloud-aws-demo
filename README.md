# spring-cloud-aws-demo
Demo projects for Spring clients applications for AWS

### Build
To build any service as a `jar` use
```shell
./gradlew -p :project-folder build -x test
```
You can run tests that are using Docker with 
```shell
./gradlew -p :project-folder build
```

### Test

```shell
http :9091/actuator/health
http -j :9090/message uuid=id recipientId=recipient body='hello sqs'
```
