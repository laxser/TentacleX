package com.laxser.tentaclex.response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.laxser.tentaclex.ContentParseException;
import com.laxser.tentaclex.TentacleResponse;
import com.laxser.tentaclex.jackson.ObjectMapperFactory;

/**
 * XoaResponse的默认实现
 * 
 * @author laxser  Date 2012-5-31 下午4:48:45
@contact [duqifan@gmail.com]
@DefaultTentacleResponse.java

 */
public class DefaultTentacleResponse implements TentacleResponse {

	private int statusCode;
	
	private Map<String, String> headers;
	
	private byte[] content;

	private String remoteHost;

	public void setHeader(String headerName, String headerValue) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(headerName, headerValue);
	}
	
	@Override
	public String getHeader(String headerName) {
		if (headers == null) {
			return null;
		}
		return headers.get(headerName);
	}

	public void setStatusCode(int statusCode) { 
		this.statusCode = statusCode;
	}
	
	@Override
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getContentEncoding() {
		return this.getHeader(HttpHeaders.Names.CONTENT_ENCODING);
	}

	public String getContentType() {
		return this.getHeader(HttpHeaders.Names.CONTENT_TYPE);
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}

	/*@Override
	public JSONObject getContentAsJsonObject() {
		try {
			JSONTokener jt = new JSONTokener(new BufferedReader(
					new InputStreamReader(getContentAsInputStream())));
			return new JSONObject(jt);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	@Override
	public JSONArray getContentAsJsonArray() {
		try {
			JSONTokener jt = new JSONTokener(new BufferedReader(
					new InputStreamReader(getContentAsInputStream())));
			return new JSONArray(jt);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public <T> T getContent(JoOMapper<T> mapper) {
		
		return mapper.map(getContentAsJsonObject());
	}
	
	@Override
	public <T> T getContent(JaOMapper<T> mapper) {
		return mapper.map(getContentAsJsonArray());
	}*/

	@Override
	public String getContentAsString() {

		if (content == null) {
			return null;
		}
		if (content.length == 0) {
			return "";
		}
		
		if (isCompressed()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						getContentAsInputStream(), "UTF-8"));
				StringWriter sw = new StringWriter(content.length * 2);
				char[] buff = new char[512];
				int len;
				while ((len = reader.read(buff)) != -1) {
					sw.write(buff, 0, len);
				}
				return sw.toString();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		} else {
			return new String(content);
		}
	}
	
	private boolean isCompressed() {
		return isGzipCompressed() || isDeflateCompressed();
	}

	private InputStream getContentAsInputStream() throws IOException {
		if (isGzipCompressed()) {
			return getContentAsGZIPInputStream();
		} else if (isDeflateCompressed()) {
			return getContentAsDeflateInputStream();
		} else {
			return new ByteArrayInputStream(content);
		}
	}
	
	private InputStream getContentAsDeflateInputStream() {
		return new InflaterInputStream(new ByteArrayInputStream(content));
	}

	private GZIPInputStream getContentAsGZIPInputStream() throws IOException {
		return new GZIPInputStream(new ByteArrayInputStream(content));
	}
	
	private boolean isGzipCompressed() {
		return "gzip".equals(getContentEncoding());
	}
	
	private boolean isDeflateCompressed() {
		return "deflate".equals(getContentEncoding());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getContentAs(Class<T> klass) throws ContentParseException{
		
		if (klass == String.class) {
			return (T)getContentAsString();
		}
		
		if (content == null || content.length == 0) {	//没有数据就return null
			return null;
		}
		
		if (getContentType().contains("application/protoc-buff")) {	//protocol buffers
			if (klass.getSuperclass() ==  com.google.protobuf.GeneratedMessage.class) {
				try {
					//通过反射获取parseFrom方法
					Method parseFromMethod = klass.getMethod("parseFrom", java.io.InputStream.class);
					return (T)parseFromMethod.invoke(null, this.getContentAsInputStream());
				} catch (Exception e) {
					throw new ContentParseException(e);
				}
			} else {
				throw new IllegalArgumentException(
						klass.getName()
								+ " should be subclass of com.google.protobuf.GeneratedMessage");
			}
		} else if (getContentType().contains("application/serializable")) {	//java serialization
			
			try {
				ObjectInputStream oin = new ObjectInputStream(this.getContentAsInputStream());
				Object obj = oin.readObject();
				return (T)obj;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		} else {	//json or text
			
			ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();
			InputStream is = null;
			try {
				is = getContentAsInputStream();
				return mapper.readValue(is, klass);
			} catch (Exception e) {
				throw new ContentParseException(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
/*	@Override
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getContentAsMap(Class<K> keyKlass,
			Class<V> valueKlass) throws ContentParseException {
		
		if (content == null || content.length == 0) {	//没有数据就return null
			return null;
		}
		
		if (getContentType().contains("application/protoc-buff")) {	//protocol buffers
			throw new UnsupportedOperationException("content type application/protoc-buff not supported currently.");
		} else if (getContentType().contains("application/serializable")) {	//java serialization
			
			try {
				ObjectInputStream oin = new ObjectInputStream(this.getContentAsInputStream());
				Object obj = oin.readObject();
				return (Map<K, V>)obj;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			
			ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();
			InputStream is = null;
			try {
				is = getContentAsInputStream();
				return mapper.readValue(is, new TypeReference<HashMap<String, Blog>>(){});
			} catch (Exception e) {
				throw new ContentParseException(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
*/
	
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	@Override
	public String getRemoteHost() {
		return this.remoteHost;
	}
}
