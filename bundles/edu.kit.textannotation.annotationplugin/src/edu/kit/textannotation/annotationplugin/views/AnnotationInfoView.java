package edu.kit.textannotation.annotationplugin.views;

import java.util.Comparator;
import java.util.function.Consumer;

import edu.kit.textannotation.annotationplugin.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.LayoutUtilities;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.profile.MetaDataContainer;
import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.osgi.framework.FrameworkUtil;

import javax.inject.Inject;


public class AnnotationInfoView extends ViewPart {
    public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationInfoView";

    public EventManager<EventManager.EmptyEvent> onChangedMetaData = new EventManager<>("infoview:changedmeta");

    private AnnotationTextEditor editor;
    private LayoutUtilities lu = new LayoutUtilities();

    @Inject IWorkbench workbench;

    @Override
    public void createPartControl(Composite parent) {
        AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);

        Consumer<SingleAnnotation> onHover = s -> Display.getDefault().syncExec(() -> rebuildContent(parent, s));
        Consumer<EventManager.EmptyEvent> onUnHover = v -> Display.getDefault().syncExec(() -> rebuildContent(parent, null));

        finder.annotationEditorActivated.addListener(e -> {
            e.onClickAnnotation.addListener(onHover);
            e.onClickOutsideOfAnnotation.addListener(onUnHover);
            editor = e;
        });

        finder.annotationEditorDeactivated.addListener(editor -> {
            editor.onClickAnnotation.removeListener(onHover);
            editor.onClickOutsideOfAnnotation.removeListener(onUnHover);
        });

        onChangedMetaData.addListener(e -> editor.markDocumentAsDirty());
    }

    @Override
    public void setFocus() {
    }

    @Override
    public String getTitle() {
        return "Annotation Controls";
    }

    private void rebuildContent(Composite parent, @Nullable SingleAnnotation hoveringAnnotation) {
        if (parent.isDisposed()) {
            return;
        }

        EclipseUtils.clearChildren(parent);

        if (hoveringAnnotation == null) {
            parent.layout();
            return;
        }

        Composite container = new Composite(parent, SWT.NULL); // TODO ScrollComposite
        container.setLayout(lu.gridLayout().withNumCols(1).get());
        container.setLayoutData(lu.completelyFillingGridData());

        Header.withTitle("Annotation information").render(container);

        MetaDataView annotationDataForm = new MetaDataView(
                container,
                MetaDataContainer.fromEmpty()
                        .withEntry("Marked Text", editor.getAnnotationContent(hoveringAnnotation))
                        .withEntry("Annotated Class", hoveringAnnotation.getAnnotationIdentifier())
                        .withEntry("Location", String.format("%s:%s", hoveringAnnotation.getOffset(), hoveringAnnotation.getLength())),
                false,
                false,
                false,
                false
        );

        try {
            MetaDataContainer profileMetaData = editor.getAnnotationProfile()
                    .getAnnotationClass(hoveringAnnotation.getAnnotationIdentifier()).metaData;

            Header.withTitle("Profile meta data")
                    .withSubTitle("You can edit this data in the profile editor").render(container);

            MetaDataView profileMetaDataForm = new MetaDataView(
                    container,
                    profileMetaData,
                    false,
                    false,
                    false,
                    false
            );
        } catch (Exception e) {
            // If problem with profile occurs, it's ok to not show that view.
            e.printStackTrace();
        }

        Header.withTitle("Annotation meta data")
                .withSubTitle("This annotation data is attached with the specific annotation").render(container);

        MetaDataView annotationMetaDataForm = new MetaDataView(
                container,
                hoveringAnnotation.metaData,
                true,
                true,
                true,
                true
        );

        hoveringAnnotation.metaData.onChange.attach(onChangedMetaData);
        annotationDataForm.onChangedMetaData.addListener(e -> {
            container.layout();
            parent.layout();
        });

        container.layout();
        parent.layout();
    }
}