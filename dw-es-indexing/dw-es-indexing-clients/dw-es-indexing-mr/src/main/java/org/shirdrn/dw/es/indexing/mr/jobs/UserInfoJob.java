package org.shirdrn.dw.es.indexing.mr.jobs;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserInfoJob {
	
	public UserInfoJob() {
		
	}

	public static class UserInfoMapper extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
		
		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, NullWritable, NullWritable>.Context context)
						throws IOException, InterruptedException {
			
		}
	}
	
	public static void main(String[] args) {
		
	}

}
