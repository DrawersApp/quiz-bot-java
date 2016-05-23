import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by harshit on 21/1/16.
 */
public interface TriviaInterface {

    @Headers({
            "Accept: application/json",
    })
    @GET("/v1/getAllQuizQuestions")
    List<Question> getAllQuestions(@Query("limit") int limit,
                                   @Query("page") int page);
}
