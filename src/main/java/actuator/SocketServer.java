package actuator;



import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class SocketServer {
    //private final static Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    public static int Threadnum=100;
    public static BufferedWriter writer;
    public static String close="0";
    public static String jostr;
    public static FileReader reader1;


    //初始化配置文件
    static {
        try {
            //reader1 = new FileReader("./cfg.csv");
            reader1 = new FileReader("E:\\cfg.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static BufferedReader br = new BufferedReader(reader1);

    static {
        try {
            jostr = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> result = Arrays.asList(jostr.split(","));

    public static void main(String[] args) throws IOException {
            //单独线程处理服务端命令
            //new Thread(new ReceiveCmd()).start();

            //消费
            Log4jUtil.info("consumer init");
            Properties props = new Properties();
            props.put("bootstrap.servers", result.get(0));
            //props.put("bootstrap.servers", "172.19.4.152:9092");
            //props.put("bootstrap.servers", "192.168.120.158:9092");//单节点，kafka多节点时候使用,逗号隔开
            props.put("group.id", "1111"); //定义消费组
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            //props.put("zookeeper.session.timeout.ms", "10000000");
            props.put("auto.offset.reset", "earliest");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Arrays.asList("sendhttp"));//订阅主题



            Log4jUtil.debug("producer init");
            //生产
            Properties propsb = new Properties();

//            propsb.put("bootstrap.servers", result.get(1));
//            propsb.put("acks", "1");
//            propsb.put("group.id", "1111");
//            propsb.put("retries", "1000");
//            propsb.put("linger.ms", "1");
//            //props.put("zookeeper.session.timeout.ms", "1000");
//            propsb.put("buffer.memory", "33554432");
//            //设置key和value序列化方式
//            propsb.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//            propsb.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

              propsb.put("zookeeper.connect", result.get(1));// 声明zookeeper
              propsb.put("bootstrap.servers", result.get(1));
              propsb.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
              propsb.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");



            KafkaProducer<String, String> producer = new KafkaProducer<String, String>(propsb);
            //Threadnum是需要启动的线程数
            ExecutorService pool = Executors.newFixedThreadPool(Threadnum);

            Log4jUtil.debug("Service is running");

            try{
                while (true) {
                    //获取消息队列
                    ConsumerRecords<String, String> records = consumer.poll(2000);
                    for (ConsumerRecord<String, String> record : records) {

                        //消息队列不为空才能提交线程
                        if (records.count() != 0){
                            Log4jUtil.info("request:");
                            Log4jUtil.info(record.value());
                            Log4jUtil.debug("submit Threadpool");
                            Log4jUtil.debug(record.value());
                            pool.submit(new MyRuns(record.value(),producer));
                        }
                        //如果关闭执行器的标记位是就关闭执行器
                        if (close =="1"){
                            consumer.close();
                            pool.shutdown();
                            Log4jUtil.debug("close service");
                            return;
                        }
                    }

                }
            } finally {
                consumer.close();
                producer.close();
            }

        }
    }
