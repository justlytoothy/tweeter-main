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
import edu.byu.cs.tweeter.server.service.Security;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAO extends BaseDAO implements IUserDAO {

    public UserDAO() {
        this.initializeDatabase();
        this.switchTable("user");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        //TODO can compare with .equals()
        String password = Security.encryptPassword(request.getPassword());


        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new AuthResponse(user, authToken);
    }

    @Override
    public AuthResponse signup(SignupRequest request) {

        // TODO: Generates dummy data. Replace with a real implementation.
        String encryptedPassword = Security.encryptPassword(request.getPassword());
        String link = storeImage(request.getImage(),request.getUsername());
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new AuthResponse(user, authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        return new GetUserResponse(getFakeData().findUserByAlias(request.getUsername()));
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


}
