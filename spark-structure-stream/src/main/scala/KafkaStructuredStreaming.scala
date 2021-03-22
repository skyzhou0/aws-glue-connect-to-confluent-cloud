import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

import com.amazonaws.services.glue.GlueContext
import com.amazonaws.services.glue.MappingSpec
import com.amazonaws.services.glue.errors.CallSite
import com.amazonaws.services.glue.util.GlueArgParser
import com.amazonaws.services.glue.util.Job
import com.amazonaws.services.glue.util.JsonOptions
import com.amazonaws.services.glue.DynamicFrame
import org.apache.spark.SparkContext
import scala.collection.JavaConverters._

object KafkaStructuredStreaming {

  def main(args: Array[String]): Unit = {

    //    Spark Streaming context:
    val spark = SparkSession.builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    val sparkCont: SparkContext = new SparkContext()
    val glueContext: GlueContext = new GlueContext(sparkCont)
    val args = GlueArgParser.getResolvedOptions(sysArgs, Seq("JOB_NAME").toArray)
    Job.init(args("JOB_NAME"), glueContext, args.asJava)

    import spark.implicits._

    val someDF = Seq(
      ("hell", "bat"),
      ("test", "mouse"),
      ("test", "horse")
    ).toDF("value", "key")

    someDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .write
      .format("org.apache.spark.sql.kafka010.KafkaSourceProvider")
      .option("kafka.bootstrap.servers", "cluster_url")
      .option("topic", "confluent-cloud-kafka-topic")
      .option("kafka.security.protocol", "SASL_SSL")
      .option("kafka.sasl.mechanism", "PLAIN")
      .option("kafka.sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='<API_KEY>' password='<API_SECRET>';")
      .save

    someDF.printSchema()
  }

}