package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import org.eclipse.jface.text.IDocument;

public class TextModelData {
	final EventManager<String> onChangeProfile = new EventManager<>();

	private AnnotationSet annotations;
	private String profileName;
	private IDocument document;

	TextModelData(AnnotationSet annotations, String profileName, IDocument document) {
		this.setAnnotations(annotations);
		this.profileName = profileName;
		this.setDocument(document);
	}

	@Override
	public String toString() {
		return String.format("AnnotationData(%s, %s)", getAnnotations().toString(), getProfileName());
	}


	private AnnotationProfile getProfile(AnnotationProfileRegistry registry) {
		return registry.findProfile(getProfileName());
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
		onChangeProfile.fire(profileName);
	}

	public AnnotationSet getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationSet annotations) {
		this.annotations = annotations;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}
}