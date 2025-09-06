package co.com.jhompo.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class ApprovedCountEntity {

    private String id;
    private String atr1;

    public ApprovedCountEntity() {
    }

    public ApprovedCountEntity(String id, String atr1) {
        this.id = id;
        this.atr1 = atr1;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("count")
    public String getAtr1() {
        return atr1;
    }

    public void setAtr1(String atr1) {
        this.atr1 = atr1;
    }
}
