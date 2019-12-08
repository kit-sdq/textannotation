package edu.kit.textannotation.annotationplugin;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationData;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import javax.inject.Inject;


public class AnnotationControlsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationControlsView";
	private GridLayout layout;
	private Combo profileSelector;
	// private Button buttonMakeSubstantive;
	// private Button buttonMakeVerb;
	// private Button buttonMakeObjective;
	private Button buttonEditProfile;
	private Button buttonNewProfile;
	private Composite annotationDataButtonsComposite;
	private Composite parent;

	@Inject IWorkbench workbench;
	
	@Inject EModelService modelService;    
	@Inject MWindow window;
	
	public AnnotationControlsView() {
		super();

	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);
		finder.annotationEditorActivated.addListener(editor -> rebuildContent(parent, editor.getAnnotationData()));
		if (finder.getAnnotationEditor() != null) {
			rebuildContent(parent, finder.getAnnotationEditor().getAnnotationData());
		}
		
		/*this.parent = parent;
		// TODO do something with editor
		
		layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Composite selectorComposite = new Composite(parent, SWT.NONE);
		selectorComposite.setLayout(new GridLayout(3, false));

		annotationDataButtonsComposite = new Composite(parent, SWT.NONE);
		annotationDataButtonsComposite.setLayout(new GridLayout());

		profileSelector = new Combo(selectorComposite, SWT.DROP_DOWN | SWT.BORDER);
		

		AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);
		finder.annotationEditorActivated.addListener(editor -> updateAnnotationData(editor.getAnnotationData()));
		if (finder.getAnnotationEditor() != null) {
			updateAnnotationData(finder.getAnnotationEditor().getAnnotationData());
		}
		
		// buttonMakeSubstantive = new Button(parent, SWT.PUSH | SWT.FILL);
		// buttonMakeVerb = new Button(parent, SWT.PUSH | SWT.FILL);
		// buttonMakeObjective = new Button(parent, SWT.PUSH | SWT.FILL);
		
		buttonEditProfile = new Button(selectorComposite, SWT.PUSH);
		buttonNewProfile = new Button(selectorComposite, SWT.PUSH);

        profileSelector.add("Requirements Engineering");
        profileSelector.add("Text Analysis");
        profileSelector.add("Add new profile...");

		// buttonMakeSubstantive.setText("Make Substantive");
        // buttonMakeVerb.setText("Make Verb");
        // buttonMakeObjective.setText("Make Objective");
		buttonEditProfile.setText("Edit Profile");
		buttonNewProfile.setText("New Profile");
		
		buttonEditProfile.addListener(SWT.Selection, new Listener() {
		    @Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
		    	AnnotationProfile demoProfile = new AnnotationProfile("Default Profile");
		    	demoProfile.addAnnotationClass(new AnnotationClass("Substantive", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Verb", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Objective", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Other", new Color(Display.getCurrent(), 255, 0, 0)));
		    	EditProfileDialog.openWindow(demoProfile);
		    }
		});
		
		for (Control c: Arrays.asList(selectorComposite, profileSelector)) {
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}*/
	}

	@Override
	public void setFocus() {
	}
	
	@Override
	public String getTitle() {
		return "Annotation Controls";
	}
	
	private void rebuildContent(Composite parent, AnnotationData annotationData) {
		for (Control child: parent.getChildren()) {
			child.dispose();
		}
		
		layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Composite selectorComposite = new Composite(parent, SWT.NONE);
		selectorComposite.setLayout(new GridLayout(3, false));

		profileSelector = new Combo(selectorComposite, SWT.DROP_DOWN | SWT.BORDER);
		
		
		buttonEditProfile = new Button(selectorComposite, SWT.PUSH);
		buttonNewProfile = new Button(selectorComposite, SWT.PUSH);

        profileSelector.add("Requirements Engineering");
        profileSelector.add("Text Analysis");
        profileSelector.add("Add new profile...");
        
		buttonEditProfile.setText("Edit Profile");
		buttonNewProfile.setText("New Profile");
		
		buttonEditProfile.addListener(SWT.Selection, new Listener() {
		    @Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
		    	AnnotationProfile demoProfile = new AnnotationProfile("Default Profile");
		    	demoProfile.addAnnotationClass(new AnnotationClass("Substantive", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Verb", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Objective", new Color(Display.getCurrent(), 255, 0, 0)));
		    	demoProfile.addAnnotationClass(new AnnotationClass("Other", new Color(Display.getCurrent(), 255, 0, 0)));
		    	EditProfileDialog.openWindow(demoProfile);
		    }
		});
		
		for (Control c: Arrays.asList(selectorComposite, profileSelector)) {
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}

		for (AnnotationClass a: annotationData.profile.getAnnotationClasses()) {
			Button b = new Button(parent, SWT.PUSH | SWT.FILL);
			b.setText(a.getName());
			b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			b.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					new AnnotationEditorFinder(workbench).getAnnotationEditor().annotate(a);
				}
			});
		}

		// TODO how to properly redraw parent s.t. widths are properly aligned?
		parent.pack();
		parent.layout();
		parent.redraw();
		parent.update();
	}
	
	/*private void updateAnnotationData(AnnotationData annotationData) {
		if (annotationDataButtons != null) {
			annotationDataButtons.stream().forEach(b -> b.dispose());
		}
		annotationDataButtons = new ArrayList<Button>(annotationData.profile.getAnnotationClasses().size());
		
		for (AnnotationClass a: annotationData.profile.getAnnotationClasses()) {
			System.out.println("New button");
			Button b = new Button(annotationDataButtonsComposite, SWT.PUSH | SWT.FILL);
			b.setText(a.getName());
			b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			b.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					new AnnotationEditorFinder(workbench).getAnnotationEditor().annotate(a);
				}
			});
			annotationDataButtons.add(b);
		}
		
		parent.pack();
	}*/
}
