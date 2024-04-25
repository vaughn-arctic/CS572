import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.lang.Throwable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class BIGRAMS {
    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>
    {
        private Text docID = new Text();
        private Text bigrams = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException
        {
            String[] targetStrings = {
                    "computer science",
                    "information retrieval",
                    "power politics",
                    "los angeles",
                    "bruce willis"
            };
            String[] document = value.toString().split("\t", 2);
            /*Handle Charachters, Strings, Spaces Etc */
            String words = document[1].toLowerCase();
            words = words.replaceAll("[^a-z\\s]", " ");
            words = words.replaceAll("\\s+", " ");

            docID.set(document[0]);
            StringTokenizer itr = new StringTokenizer(words);
            String last = itr.nextToken();
            while (itr.hasMoreTokens())
            {
                String current = itr.nextToken();
                bigrams.set(last + " " + current);
                String bigramsString = bigrams.toString();
                for (String target : targetStrings) {
                    if (bigramsString.equalsIgnoreCase(target)) {
                        context.write(bigrams, docID);
                    }
                }
                last = current;
            }
        }
    }

    public static class indexReducer extends Reducer<Text, Text, Text, Text>
    {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
            HashMap<String, Integer> newCounter = new HashMap<>();
            for (Text val:values) {
                String docID = val.toString();
                newCounter.put(docID, newCounter.getOrDefault(docID, 0) + 1);
            }

            StringBuilder s = new StringBuilder();
            for (String k : newCounter.keySet()) {
                s.append(k).append(":").append(newCounter.get(k)).append("\t");

                result.set(s.substring(0, s.length() - 1));
                context.write(key, result);
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");


        job.setJarByClass(BIGRAMS.class);
        job.setMapperClass(TokenizerMapper.class);
        /*  job.setCombinerClass(indexReducer.class); */
        job.setReducerClass(indexReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}// WordCount

