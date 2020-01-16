package net.devtech.patchdoc.files;

import java.util.Map;
import java.util.Objects;

public class FileInfo {
	public FileStatus status;
	public String file;
	public Map<Integer, String> comments;

	public FileInfo(FileStatus status, String file, Map<Integer, String> comments) {
		this.status = status;
		this.file = file;
		this.comments = comments;
	}


	public FileStatus getStatus() {
		return this.status;
	}

	public String getFile() {
		return this.file;
	}

	public Map<Integer, String> getComments() {
		return this.comments;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof FileInfo)) return false;
		FileInfo info = (FileInfo) object;
		return this.status == info.status && Objects.equals(this.file, info.file) && Objects.equals(this.comments, info.comments);
	}

	@Override
	public int hashCode() {
		int result = this.status != null ? this.status.hashCode() : 0;
		result = 31 * result + (this.file != null ? this.file.hashCode() : 0);
		result = 31 * result + (this.comments != null ? this.comments.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "FileInfo{" + "status=" + this.status + ", file='" + this.file + '\'' + ", comments=" + this.comments + '}';
	}
}
