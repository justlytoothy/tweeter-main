package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.server.service.Security;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class UserDAO extends BaseDAO implements IUserDAO {

    public UserDAO() {
        this.initializeDatabase();
        this.switchTable("user");
    }
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @Override
    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        String password = Security.encryptPassword(request.getPassword());
        UserBean user = getUserUsername(request.getUsername());
        if (Security.encryptPassword(user.getPassword()).equals(password)) {
            AuthToken authToken = getDummyAuthToken();
            return new AuthResponse(userToUser(user), authToken);
        }
        else {
//            return new AuthResponse("Incorrect password");
            //TODO return error response
            AuthToken authToken = getDummyAuthToken();
            return new AuthResponse(userToUser(user), authToken);
        }

    }

    @Override
    public AuthResponse signup(SignupRequest request) {

        // TODO: Generates dummy data. Replace with a real implementation.
        String encryptedPassword = Security.encryptPassword(request.getPassword());
        String link = storeImage(request.getImage(),request.getUsername());
        DynamoDbTable<UserBean> table = enhancedClient.table("user",TableSchema.fromBean(UserBean.class));
        UserBean newUser = new UserBean();
        newUser.setFirst_name(request.getFirstName());
        newUser.setLast_name(request.getLastName());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(encryptedPassword);
        newUser.setImage(link);
        table.putItem(newUser);
        User user = userToUser(getUserUsername(request.getUsername()));
        AuthToken authToken = getDummyAuthToken();
        return new AuthResponse(user, authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        User user = userToUser(getUserUsername(request.getUsername()));
        return new GetUserResponse(user);
    }
    public UserBean getUserUsername(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table("user", TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();
        UserBean user = table.getItem(key);
        return user;
    }


    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private String storeImage(String image, String alias) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-1")
                .build();
        byte[] byteArray = Base64.getDecoder().decode(image);
        ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(byteArray.length);
        data.setContentType("image/jpeg");
        PutObjectRequest request = new PutObjectRequest("tweeter-images-jschilling", alias, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);
        return "https://tweeter-images-jschilling.s3.us-east-1.amazonaws.com/" + alias;
    }
    private User userToUser(UserBean userBean) {
        return new User(userBean.getFirst_name(), userBean.getLast_name(), userBean.getUsername(), userBean.getImage());
    }


}
