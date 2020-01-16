package net.devtech.patchdoc.gui.view;

import net.devtech.utilib.functions.ThrowingBiConsumer;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class JIconRowHeader extends IconRowHeader {
	private static final ImageIcon COMMENT_ICON = new ImageIcon(JIconRowHeader.class.getResource("/comment.png"));
	private Map<Integer, String> comments = new HashMap<>();
	/**
	 * Constructor.
	 *
	 * @param textArea The parent text area.
	 */
	public JIconRowHeader(RTextArea textArea) {
		super(textArea);
	}

	public void setComments(Map<Integer, String> comments) {
		this.comments = comments;
		this.getGutter().removeAllTrackingIcons();
		comments.forEach((ThrowingBiConsumer<Integer, String>) (i, s) -> this.getGutter().addLineTrackingIcon(i, COMMENT_ICON, s));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			int line = this.viewToModelLine(e.getPoint());
			GutterIconInfo[] infos = this.getTrackingIcons(line);
			if (infos.length == 1) {
				if(JOptionPane.showConfirmDialog(null, "delete comment?", "Comment", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
					this.getGutter().removeTrackingIcon(infos[0]);
					this.comments.remove(line);
				}
			} else if(infos.length == 0) {
				String comment = JOptionPane.showInputDialog(null, "write comment");
				if(comment != null && !comment.isEmpty()) {
					this.getGutter().addLineTrackingIcon(line, COMMENT_ICON, comment);
					this.comments.put(line, comment);
				}
			}
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	private int viewToModelLine(Point p) throws BadLocationException {
		int offs = this.textArea.viewToModel(p);
		return offs > -1 ? this.textArea.getLineOfOffset(offs) : -1;
	}
}
