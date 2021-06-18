package cn.linked.commonlib.promise;

public enum PromiseState {
    PENDING(1, "进行中"),FULFILLED(2, "已成功"),
    REJECTED(3, "已失败");

    private int index;
    private String description;

    private PromiseState(int index,String description) {
        this.index = index;
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "{PromiseState:" + super.toString() + ",description:" + description + "}";
    }
}
