package edu.byu.cs.tweeter.server.dao.beans;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.service.Security;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@DynamoDbBean
public class UserBean {
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String image;

    public UserBean(User u) {
        username = u.getAlias();
        password = Security.encryptPassword(u.getAlias());
        first_name = u.getFirstName();
        last_name = u.getLastName();
        image = u.getImageUrl();
    }
    public UserBean(){}

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public String getLast_name() {
        return last_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name=" + last_name +
                ", image='" + image + '\'' +
                '}';
    }
}