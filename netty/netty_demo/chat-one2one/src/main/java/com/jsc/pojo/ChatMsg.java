package com.jsc.pojo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "chat_msg")
public class ChatMsg {

    private static final long serialVersionUID = -6711813815322058439L;

    /*@GenericGenerator是Hibernate提供的主键生成策略注解，
    注意下面的@GeneratedValue（JPA注解）使用generator = "idGenerator"引用了上面的name = "idGenerator"主键生成策略*/
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id; //消息主键

    @Column(name = "sender_id")
    private String senderId; //发送者的用户id

    @Column(name = "receive_id")
    private String receiveId; //接受者的用户id
    private String msg; //聊天内容
    @Column(name = "msg_id")
    private String msgId; //用于消息的签收
    private Integer statue; //消息签收状态

    /*
      JPA自带的几种主键生成策略
      TABLE： 使用一个特定的数据库表格来保存主键
      SEQUENCE： 根据底层数据库的序列来生成主键，条件是数据库支持序列。这个值要与generator一起使用，generator 指定生成主键使用的生成器（可能是orcale中自己编写的序列）
      IDENTITY： 主键由数据库自动生成（主要是支持自动增长的数据库，如mysql）
      AUTO： 主键由程序控制，也是GenerationType的默认值*/

}
