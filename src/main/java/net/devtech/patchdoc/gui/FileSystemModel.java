package net.devtech.patchdoc.gui;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

public class FileSystemModel implements TreeModel {
	private final File root; // The root identifier

	public FileSystemModel(File root) {
		this.root = root;
	}

	@Override
	public Object getRoot() {
		return this.root;
	}

	@Override
	public Object getChild(Object parent, int index) {
		File directory = (File) parent;
		String[] directoryMembers = directory.list();
		assert directoryMembers != null;
		return (new File(directory, directoryMembers[index]));
	}

	@Override
	public int getChildCount(Object parent) {
		File fileSystemMember = (File) parent;
		if (fileSystemMember.isDirectory()) {
			String[] directoryMembers = fileSystemMember.list();
			assert directoryMembers != null;
			return directoryMembers.length;
		} else {

			return 0;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		File directory = (File) parent;
		File directoryMember = (File) child;
		String[] directoryMemberNames = directory.list();
		int result = -1;

		assert directoryMemberNames != null;
		for (int i = 0; i < directoryMemberNames.length; ++i) {
			if (directoryMember.getName().equals(directoryMemberNames[i])) {
				result = i;
				break;
			}
		}

		return result;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((File) node).isFile();
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Does Nothing!
	}
}