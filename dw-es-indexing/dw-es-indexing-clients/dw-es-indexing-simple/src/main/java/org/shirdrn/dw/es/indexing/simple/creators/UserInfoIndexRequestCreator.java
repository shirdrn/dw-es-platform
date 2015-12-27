package org.shirdrn.dw.es.indexing.simple.creators;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.shirdrn.dw.es.indexing.api.ContentBuilder;
import org.shirdrn.dw.es.indexing.api.DocContent;
import org.shirdrn.dw.es.indexing.common.AbstractIndexRequestCreator;
import org.shirdrn.dw.es.indexing.common.GenericDocContent;
import org.shirdrn.dw.es.utils.DateTimeUtils;

import com.google.common.base.Preconditions;

public class UserInfoIndexRequestCreator extends AbstractIndexRequestCreator<String, XContentBuilder> {

	private static final Log LOG = LogFactory.getLog(UserInfoIndexRequestCreator.class);
	private Iterator<IndexRequest> iterator;
	
	public UserInfoIndexRequestCreator(String index, String type) {
		super(index, type);
	}
	
	@Override
	public Iterator<IndexRequest> iterator() {
		Preconditions.checkArgument(indexingDataSource != null, "indexingDataSource == null");
		iterator = indexingDataSource.createIterator();
		return iterator;
	}
	
	@Override
	public ContentBuilder<String, XContentBuilder> newContentBuilder() {
		return new UserInfoContentBuilder();
	}
	
	private final class UserInfoContentBuilder implements ContentBuilder<String, XContentBuilder> {

		@Override
		public DocContent<XContentBuilder> build(String record) {
			DocContent<XContentBuilder> content = null;
			try {
				String[] a = record.split("\t", -1);
				String id = a[0];
				if(a.length >= 22 && !id.trim().isEmpty()) {
					content = new GenericDocContent<XContentBuilder>(id);
					content.setContent(createSource(a));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return content;
		}
		
		private XContentBuilder createSource(String[] a) throws IOException {
			String ts = a[17];
			if(ts.length() != 13) {
				ts = "0000-01-01 00:00:00";
			}
			XContentBuilder builder = jsonBuilder()
			        .startObject()
						.field("channel", normalize(a[1]))
						.field("current_channel", normalize(a[2]))
						.field("channel_type", normalize(a[3]))
						.field("device_type", normalize(a[4]))
						.field("device_name", normalize(a[5]))
						.field("version", normalize(a[6]))
						.field("os_version", normalize(a[7]))
						.field("carrier_operator", normalize(a[8]))
						.field("network", normalize(a[9]))
						.field("resolution", normalize(a[10]))
						.field("ip", normalize(a[11]))
						.field("area_code", normalize(a[12]))
						.field("login_date", checkDate(normalize(a[13])))
						.field("last_date", checkDate(normalize(a[14])))
						.field("first_open_date", checkDate(normalize(a[15])))
						.field("first_play_date", checkDate(normalize(a[16])))
						.field("create_time", DateTimeUtils.format(Long.parseLong(ts), "yyyy-MM-dd HH:mm:ss"))
						.field("first_open_or_play_date", checkDate(normalize(a[18])))
						.field("mac", normalize(a[19]))
						.field("idfa", normalize(a[20]))
						.field("imei", normalize(a[21]))
			        .endObject();
			LOG.debug(builder.toString());
			return builder;
		}
		
		private String checkDate(String d) {
			if(d == null || d.length() != 10) {
				return "0000-01-01";
			}
			return d;
		}
		
		private String normalize(String str) {
			if(str != null) {
				if(str.equalsIgnoreCase("null")) {
					return "";
				}
			}
			if(str == null) {
				return "";
			}
			return str;
		}
		
	}
	
	@Override
	public void close() throws IOException {
		indexingDataSource.close();		
	}
	
	@Override
	public void reset() {
		super.reset();
	}

}
