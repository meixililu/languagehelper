package com.messi.languagehelper.http;

import java.io.IOException;

import com.avos.avoscloud.okhttp.MediaType;
import com.avos.avoscloud.okhttp.ResponseBody;
import com.avos.avoscloud.okio.Buffer;
import com.avos.avoscloud.okio.BufferedSource;
import com.avos.avoscloud.okio.ForwardingSource;
import com.avos.avoscloud.okio.Okio;
import com.avos.avoscloud.okio.Source;
import com.messi.languagehelper.inteface.ProgressListener;

public class ProgressResponseBody extends ResponseBody {

	private final ResponseBody responseBody;
	private final ProgressListener progressListener;
	private BufferedSource bufferedSource;

	public ProgressResponseBody(ResponseBody responseBody,
			ProgressListener progressListener) {
		this.responseBody = responseBody;
		this.progressListener = progressListener;
	}

	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() throws IOException {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() throws IOException {
		if (bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytesRead = 0L;
			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				long bytesRead = super.read(sink, byteCount);
				// read() returns the number of bytes read, or -1 if this source
				// is exhausted.
				totalBytesRead += bytesRead != -1 ? bytesRead : 0;
				progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
				return bytesRead;
			}
		};
	}

}
