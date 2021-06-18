package cn.linked.baselib.repository.protocol;

import java.util.List;

import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.HttpResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatProtocol {

    @FormUrlEncoded
    @POST("link/chatController/getChatMessage")
    Call<HttpResult<List<ChatMessage>>> getChatMessage(@Field("groupId") String groupId,
                                                       @Field("maxSequenceNumber") Long maxSequenceNumber, @Field("num") Integer num);

}
