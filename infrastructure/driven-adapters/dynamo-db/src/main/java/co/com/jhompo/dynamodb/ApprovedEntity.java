package co.com.jhompo.dynamodb;

import lombok.Builder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@Builder
@DynamoDbBean
public class ApprovedEntity {

    private String id;
    private Long  count;
    private BigDecimal totalAmount;

    public ApprovedEntity() {}

    public ApprovedEntity(String id, Long  count) {
        this.id = id;
        this.count = count;
        this.totalAmount = BigDecimal.ZERO;
    }

    public ApprovedEntity(String id, Long  count, BigDecimal totalAmount) {
        this.id = id;
        this.count = count;
        this.totalAmount = totalAmount;
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
    public Long  getCount() {
        return count;
    }

    public void setCount(Long  count) {
        this.count = count;
    }

    @DynamoDbAttribute("totalAmount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

}
