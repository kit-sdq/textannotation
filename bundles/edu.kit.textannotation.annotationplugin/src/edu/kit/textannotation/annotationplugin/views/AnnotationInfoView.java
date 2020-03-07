package edu.kit.textannotation.annotationplugin.views;

import java.util.function.Consumer;

import edu.kit.textannotation.annotationplugin.editor.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.*;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.utils.EventManager;
import edu.kit.textannotation.annotationplugin.utils.LayoutUtilities;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;

import org.eclipse.ui.*;

import javax.inject.Inject;

/**
 * The info view which is contributed to the plugin. See the plugins documentation for more details on
 * its contributing views.
 */
public class AnnotationInfoView extends ViewPart {
    public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationInfoView";

    /** This event manager fires when the meta data attached to the current annotation changes. */
    public EventManager<EventManager.EmptyEvent> onChangedMetaData = new EventManager<>("infoview:changedmeta");

    private AnnotationTextEditor editor;
    private LayoutUtilities lu = new LayoutUtilities();

    @Inject IWorkbench workbench;

    @Override
    public void createPartControl(Composite parent) {
        AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);

        Consumer<SingleAnnotation> onHover = s -> Display.getDefault().syncExec(() -> rebuildContent(parent, s));
        Consumer<EventManager.EmptyEvent> onUnHover = v -> Display.getDefault().syncExec(() -> rebuildContent(parent, null));

        finder.onAnnotationEditorActivated.addListener(e -> {
            e.onClickAnnotation.addListener(onHover);
            e.onClickOutsideOfAnnotation.addListener(onUnHover);
            editor = e;
        });

        finder.onAnnotationEditorDeactivated.addListener(editor -> {
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

        AnnotationClass annotationClass;

        EventManager<EventManager.EmptyEvent> relayout = new EventManager<>();
        Composite container = lu.createVerticalScrollComposite(parent, relayout);
        container.setLayout(lu.gridLayout().withNumCols(1).get());
        container.setLayoutData(lu.completelyFillingGridData());

        try {
            annotationClass = editor.getAnnotationProfile().getAnnotationClass(hoveringAnnotation.getAnnotationIdentifier());
        } catch (InvalidAnnotationProfileFormatException e) {
            Header.withTitle("Error")
                    .withSubTitle("The profile is not properly formatted.").render(container);
            return;
        } catch (ProfileNotFoundException e) {
            Header.withTitle("Error")
                    .withSubTitle("The profile could not be found..").render(container);
            return;
        } catch (Exception e) {
            Header.withTitle("Error")
                    .withSubTitle(e.getMessage()).render(container);
            return;
        }

        MetaDataContainer profileMetaData = annotationClass.metaData;

        Header.withTitle(annotationClass.getName())
                .withSubTitle(annotationClass.getDescription())
                .withButton("Remove annotation", () -> {
                    editor.deannotate(hoveringAnnotation.getOffset());
                    rebuildContent(parent, null);
                })
                .render(container);

        new MetaDataView(
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

        Header.withTitle("Annotation class meta data")
                .withSubTitle("You can edit this data in the profile editor")
                .withButton("Edit in Profile Editor", () -> {
                    AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);
                    AnnotationTextEditor editor = finder.getAnnotationEditor();
                    AnnotationProfileRegistry registry = editor.getAnnotationProfileRegistry();
                    try {
                        EditProfileDialog.openWindow(registry, editor.getAnnotationProfile().getName(), profile -> {
                            rebuildContent(parent, hoveringAnnotation);
                        }, annotationClass.getName());
                    } catch (ProfileNotFoundException e) {
                        EclipseUtils.reportError("Profile could not be found.");
                    } catch (InvalidAnnotationProfileFormatException e) {
                        EclipseUtils.reportError("Profile is not properly formatted: " + e.getMessage());
                    }
                })
                .render(container);

        new MetaDataView(
                container,
                profileMetaData,
                false,
                false,
                false,
                false
        );

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

        annotationMetaDataForm.onShouldResize.addListener(e -> {
            relayout.fire(new EventManager.EmptyEvent());
            container.layout();
            parent.layout();
        });

        relayout.fire(new EventManager.EmptyEvent());
        container.layout();
        parent.layout();
    }
}
