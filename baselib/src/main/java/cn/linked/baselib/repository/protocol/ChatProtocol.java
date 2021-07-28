package cn.linked.baselib.repository.protocol;

import java.util.List;

import cn.linked.baselib.entity.ChatGroup;
import cn.linked.baselib.entity.ChatGroupMember;
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

    @FormUrlEncoded
    @POST("link/chatController/getChatGroupById")
    Call<HttpResult<ChatGroup>> getChatGroupById(@Field("groupId") String groupId);

    @FormUrlEncoded
    @POST("link/chatController/getChatGroupMember")
    Call<HttpResult<List<ChatGroupMember>>> getChatGroupMember(@Field("groupId") String groupId);

    @FormUrlEncoded
    @POST("link/chatController/getUserChatGroupMember")
    Call<HttpResult<ChatGroupMember>> getUserChatGroupMember(@Field("groupId") String groupId, @Field("userId") Long userId);

    @FormUrlEncoded
    @POST("link/chatController/setUserHaveReadMessageMaxSequenceNum")
    Call<HttpResult<Boolean>> setUserHaveReadMessageMaxSequenceNum(@Field("groupId") String groupId,
                                                                   @Field("maxSequenceNum") Long maxSequenceNum);

    @POST("link/chatController/getUserNewestChatMessage")
    Call<HttpResult<List<ChatMessage>>> getUserNewestChatMessage();

}
