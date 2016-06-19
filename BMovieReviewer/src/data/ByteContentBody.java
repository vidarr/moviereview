/**
 * BMovieReviewer Copyright (C) 2010 Michael J. Beer
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * This code was inspired by some guy from the "net" whose address I actually was 
 * unable to recover
 * If you are the one - call me :)
 */
package data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ContentBody;

public class ByteContentBody extends AbstractContentBody implements ContentBody {

    private final byte[] content;

    public ByteContentBody(final byte[] content, String mimeType, String fileName) {
        super(mimeType);
        if (content == null || fileName == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.content = content;
        this.fileName = fileName;
    }

    public InputStream getInputStream() throws IOException {
        return(new ByteArrayInputStream(content));
    }

    public void writeTo(final OutputStream out, int mode) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException();
        }
        out.write(content, 0, content.length);
    }

    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    public String getCharset() {
        return null;
    }

    public String getMimeType() {
        return super.getMimeType();
    }

    public String getMediaType() {
        return super.getMediaType();
    }

    public String getSubType() {
        return super.getSubType();
    }

    public long getContentLength() {
        return this.content.length;
    }

    public String getFilename() {
        return fileName;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        // TODO Auto-generated method stub
        this.writeTo(out, 0);
    }
    
    
    protected String fileName = null;
}
