package cn.blazeh.achat.common.model;

/**
 * 消息ID生成器接口
 */
@FunctionalInterface
public interface IIdGenerator {

    /**
     * 获取下一个消息ID
     * @return 下一个消息ID
     */
    long nextId();

}
