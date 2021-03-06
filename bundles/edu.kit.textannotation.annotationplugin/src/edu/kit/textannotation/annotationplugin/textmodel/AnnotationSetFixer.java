package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;

/**
 * This class keeps the data within an annotation set synchronized with potential document
 * events occuring to the text document, e.g. if characters are added to the beginning of
 * the document, annotations defined later in the document will be shifted by the fixer
 * accordingly.
 *
 * @see AnnotationSet
 * @see edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor
 */
public class AnnotationSetFixer {
	private AnnotationSet annotations;
	private int oldDocumentLength;
	private AnnotationTextEditor editor;

	/**
	 * Create a new fixer instance.
	 * @param annotations the set of annotations used by the document. This annotation set will by
	 *                    adapted by the fixer. It's okay for the annotationset to change after the creation
	 *                    of the fixer, as long as the reference remains the same.
	 * @param initialDocumentLength the return value of {@link IDocument::getLength} on the document.
	 * @param editor a reference on the annotation editor
	 */
	public AnnotationSetFixer(AnnotationSet annotations, int initialDocumentLength, AnnotationTextEditor editor) {
		this.annotations = annotations;
		this.oldDocumentLength = initialDocumentLength;
		this.editor = editor;
	}

	/**
	 * Adapt the annotation set accordingly to the document event
	 * @param e a document event that is performed on the text document.
	 */
	public void applyEditEvent(DocumentEvent e) {
		IDocument doc = e.getDocument();
		int docLength = doc.getLength();
		int eventStart = e.getOffset();
		int eventLength = docLength - oldDocumentLength;
		oldDocumentLength = docLength;		
		
		for (SingleAnnotation annotation: annotations.getAnnotations()) {
			if (annotation.getOffset() >= eventStart) {
				annotation.addOffset(eventLength);
			} else if (annotation.getOffset() < eventStart && annotation.getOffset() + annotation.getLength() >= eventStart) {
				annotation.addLength(eventLength);
			} else {
				// NOOP
			}
		}

		editor.markDocumentAsDirty();
	}
}
