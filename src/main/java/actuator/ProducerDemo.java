package actuator;


import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerDemo {

    // Topic
    private static final String topic = "sendhttp";
    //private static final String topic = "responsehttp";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.120.158:9092");
        props.put("acks", "1");
        props.put("group.id", "1111");
        props.put("retries", "1000");
        props.put("linger.ms", "1");
        props.put("buffer.memory", "33554432");
        //设置key和value序列化方式
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //生产者实例
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        //ProducerRecord<String, String> producerw = new ProducerRecord<String, String>(topic,"123","123");

//        producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://api.testnew.akucun.com/api/v2.0/user.do?appid=28769828&action=phonelogin&did=88e8e9527406685438961d0d348ca3d9&noncestr=0fef89\",\"method\":\"POST\",\"body\":\"{\n" +
//                "\t\\\"phonenum\\\": \\\"19621991376\\\",\n" +
//                "\t\\\"authcode\\\": \\\"1\\\",\n" +
//                "\t\\\"deviceid\\\": \\\"88e8e9527406685438961d0d348ca3d9\\\"\n" +
//                "}\",\"caseId\":\"5ceb7cd8d999965d2563e9df\",\"stepId\":\"5ceb7cded999965d2563e9e0\",\"runtimeCaseId\":\"5cebc547042c2357ca145fcd\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":1,\"taskId\":\"5cebc547042c2357ca145fc3\",\"headers\":{},\"vars\":{}}"));
//        producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://api.testnew.akucun.com/api/v2.0/user.do?appid=28769828&action=phonelogin&did=88e8e9527406685438961d0d348ca3d9&noncestr=0fef89\",\"method\":\"POST\",\"body\":\"{\n" +
//                "\t\\\"phonenum\\\": \\\"19621991376\\\",\n" +
//                "\t\\\"authcode\\\": \\\"1\\\",\n" +
//                "\t\\\"deviceid\\\": \\\"88e8e9527406685438961d0d348ca3d9\\\"\n" +
//                "}\",\"caseId\":\"5ceb7cd8d999965d2563e9df\",\"stepId\":\"5ceb7cded999965d2563e9e0\",\"runtimeCaseId\":\"5cebc547042c2357ca145fcd\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":1,\"taskId\":\"5cebc547042c2357ca145fc3\",\"headers\":{},\"vars\":{\"userid\":\"data.subuserinfos[0].userid\"}}"));
        producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://api.testnew.akucun.com/api/v2.0/user.do?appid=28769828&action=phonelogin&did=88e8e9527406685438961d0d348ca3d9&noncestr=0fef89\",\"method\":\"POST\",\"body\":\"{\n" +
                "\t\\\"phonenum\\\": \\\"19621991376\\\",\n" +
                "\t\\\"authcode\\\": \\\"1\\\",\n" +
                "\t\\\"deviceid\\\": \\\"88e8e9527406685438961d0d348ca3d9\\\"\n" +
                "}\",\"caseId\":\"5ceb7cd8d999965d2563e9df\",\"stepId\":\"5ceb7cded999965d2563e9e0\",\"runtimeStepId\":\"5cebc547042c2357ca145fcd\",\"runtimeCaseId\":\"5cebc547042c2357ca145fcd\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":1,\"taskId\":\"5cebc547042c2357ca145fc3\",\"headers\":{},\"vars\":{\"userid\":\"data.subuserinfos[0].123\"}}"));

        //producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://www.baidu.co\",\"method\":\"GET\",\"body\":null,\"caseId\":\"5ce630e212f7d38561f63709\",\"stepId\":\"5ce6442b1c2e6160288e2b20\",\"runtimeCaseId\":\"5ce6442b1c2e6160288e2b24\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":0,\"taskId\":\"5ce6442a1c2e6160288e2b1d\",\"headers\":{},\"vars\":{}}"));

        producer.flush();


//        for(int j=0;j<200;j++) {
//            //Thread.sleep(100);
//            producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://api.testnew.akucun.com/api/v2.0/user.do?appid=28769828&action=phonelogin&did=88e8e9527406685438961d0d348ca3d9&noncestr=0fef89\",\"method\":\"POST\",\"body\":\"{\n" +
//                    "\t\\\"phonenum\\\": \\\"18621991376\\\",\n" +
//                    "\t\\\"authcode\\\": \\\"1\\\",\n" +
//                    "\t\\\"deviceid\\\": \\\"88e8e9527406685438961d0d348ca3d9\\\"\n" +
//                    "}\",\"caseId\":\"5ce630e212f7d38561f63709\",\"stepId\":\"5ce6442b1c2e6160288e2b1f\",\"runtimeCaseId\":\"5ce6442b1c2e6160288e2b24\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":0,\"taskId\":\"5ce6442a1c2e6160288e2b1d\",\"headers\":{},\"vars\":{\"subUserId\":\"data.subuserinfos[0].subUserId\",\"userid\":\"data.subuserinfos[0].userid\",\"token\":\"data.subuserinfos[0].tmptoken\"}}"));
//
//            producer.send(new ProducerRecord<String, String>(topic, "zhongwen", "{\"url\":\"http://api.testnew.akucun.com/api/v2.0/cart.do?subuserid=6f0b006c4fde630b217a76d7c42bde39&appid=28769828&action=getcart&userid=6f0b006c4fde630b217a76d7c42bde39&did=1f03dbb5305c1e16b6324ac81078615c&noncestr=759414&timestamp=1550457187&token=b67a11708ce8431fa99c5010619329d2\",\"method\":\"GET\",\"body\":null,\"caseId\":\"5ce630e212f7d38561f63709\",\"stepId\":\"5ce6442b1c2e6160288e2b20\",\"runtimeCaseId\":\"5ce6442b1c2e6160288e2b24\",\"isLastRequest\":false,\"needResponse\":true,\"retry\":0,\"taskId\":\"5ce6442a1c2e6160288e2b1d\",\"headers\":{},\"vars\":{}}"));
//
//            producer.flush();
//        }

        //Thread.sleep(2000);

//        producer.send(new ProducerRecord<String, String>(topic, "key:", "value:"),new Callback(){
//                    public void onCompletion(RecordMetadata metadata, Exception e) {
//                        if(e != null) {
//                            e.printStackTrace();
//                        } else {
//                            System.out.println("offset record: " + metadata.offset());
//                        }
//                    }
//        });

        //producer.flush();

        // 发送业务消息

//        for(int j=0;j<200;j++) {
//            //Thread.sleep(100);
//            producer.send(new ProducerRecord<String, String>(topic, "key:" + i, "value:" + i));
//            System.out.println("key:" + i + " " + "value:" + i);
//        }



    }
}

