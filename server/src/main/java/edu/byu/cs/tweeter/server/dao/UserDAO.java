package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.server.service.Security;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UserDAO extends BaseDAO<UserBean> implements IUserDAO {


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
            AuthToken authToken = authorize();
            return new AuthResponse(userToUser(user), authToken);
        }
        else {
            return new AuthResponse("Incorrect password");
        }

    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        String encryptedPassword = Security.encryptPassword(request.getPassword());
        String link = storeImage(request.getImage(),request.getUsername());
        table = enhancedClient.table("user",TableSchema.fromBean(UserBean.class));
        UserBean newUser = new UserBean();
        newUser.setFirst_name(request.getFirstName());
        newUser.setLast_name(request.getLastName());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(encryptedPassword);
        newUser.setImage(link);
        table.putItem(newUser);
        User user = userToUser(getUserUsername(request.getUsername()));
        AuthToken authToken = authorize();
        return new AuthResponse(user, authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        try {
            AuthtokenBean bean = new AuthtokenBean();
            bean.setTimestamp(Long.getLong(request.getAuthToken().getDatetime()));
            bean.setToken(request.getAuthToken().getToken());
            enhancedClient.table("authtoken",TableSchema.fromBean(AuthtokenBean.class)).deleteItem(bean);
            return new LogoutResponse();
        }
        catch(Exception e) {
            return new LogoutResponse(e.getMessage());
        }

    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        try {
            if (checkAuthToken(request.getAuthToken())) {
                User user = userToUser(getUserUsername(request.getUsername()));
                return new GetUserResponse(user);
            }
            else {
                return new GetUserResponse("Invalid Auth Token");
            }
        }
        catch (Exception e) {
            return new GetUserResponse(e.getMessage());
        }

    }
    public UserBean getUserUsername(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table("user", TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();
        UserBean user = table.getItem(key);
        return user;
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

    private static boolean checkAuthToken(AuthToken authToken) {
        long currTime = System.currentTimeMillis();
        return currTime >= (Long.getLong(authToken.getDatetime())-(60*60*3*1000));
    }
    private AuthToken beanToToken(AuthtokenBean bean) {
        AuthToken token = new AuthToken();
        token.setToken(bean.getToken());
        token.setDatetime(String.valueOf(bean.getTimestamp()));
        return token;
    }
    private AuthToken authorize() {
        AuthtokenBean bean = new AuthtokenBean();
        bean.setToken(UUID.randomUUID().toString());
        bean.setTimestamp(System.currentTimeMillis());
        enhancedClient.table("authtoken",TableSchema.fromBean(AuthtokenBean.class)).putItem(bean);
        AuthToken authToken = beanToToken(bean);
        return authToken;
    }

}
