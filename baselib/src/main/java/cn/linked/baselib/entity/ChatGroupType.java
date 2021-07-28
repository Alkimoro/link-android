package cn.linked.baselib.entity;

public enum ChatGroupType {

    PRIVATE(2, ChatGroup.ID_TYPE_OBJECT_ID),
    GROUP(-1, ChatGroup.ID_TYPE_AUTO_INC),
    NOTICE_ADD_FRIEND(1, ChatGroup.ID_TYPE_OBJECT_ID),
    NOTICE_ADD_GROUP(1, ChatGroup.ID_TYPE_OBJECT_ID);

    private final int count;
    private final int idType;

    private ChatGroupType(int count, int idType) {
        this.count = count;
        this.idType = idType;
    }

    public int getCount() {
        return count;
    }
    public boolean isFixedCount() {
        return count > 0;
    }

    public int getIdType() {
        return idType;
    }

}
