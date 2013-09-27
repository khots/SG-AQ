package edu.wustl.query.bizlogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.query.queryobject.util.ExportSASDataHelper;

// Referenced classes of package edu.wustl.clinportal.dataexporter:
//            StaticExportDataHandler, DynamicExportDataHandler

/**
 * The Class GenerateSAASPGMProcessor.
 */
public class GenerateSAASPGMProcessor {

	/** Selected meta data for query */
	private SelectedColumnsMetadata selectedColumnsMetadata;
	/**
	 * 
	 */
	private List<String> columnList;

	/**
	 * 
	 */
	private String formDataFileName;

	/**
	 * The Constructor.
	 * 
	 * @param selectedColumnsMetadata
	 * @param columnList
	 * @param formDataFileName
	 * 
	 * @param staticExportDataHandler
	 *            the static export data handler
	 * @param dynamicExportDataHandler
	 *            the dynamic export data handler
	 * @param formDataFileName
	 *            the form data file name
	 */
	public GenerateSAASPGMProcessor(
			SelectedColumnsMetadata selectedColumnsMetadata,
			List<String> columnList, String formDataFileName) {
		this.selectedColumnsMetadata = selectedColumnsMetadata;
		this.columnList = columnList;
		this.formDataFileName = formDataFileName;
	}

	/**
	 * Generate saas pgm.
	 * 
	 * @return the string
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String generateSAASPgm() throws IOException {
		StringBuffer saasPgm = new StringBuffer(623);
		StringBuffer numberToStringFormat = new StringBuffer();
		StringBuffer stringToNumberFormat = new StringBuffer();
		StringBuffer informat = new StringBuffer();
		StringBuffer format = new StringBuffer();
		StringBuffer input = new StringBuffer();
		StringBuffer label = new StringBuffer();
		AttributeInterface attributeInterface;
		Iterator<String> columnListIterator = columnList.iterator();
		for (Iterator<AttributeInterface> i = getAttributeCollection()
				.iterator(); i.hasNext(); ExportSASDataHelper.generateFormat(
				informat, format, input, label, attributeInterface,
				columnListIterator.next())) {
			attributeInterface = i.next();
			ExportSASDataHelper.defineFormats(numberToStringFormat,
					stringToNumberFormat, attributeInterface);
		}

		ExportSASDataHelper.appendImportFileInfo(saasPgm, formDataFileName);
		ExportSASDataHelper.appendFormat(saasPgm, numberToStringFormat,
				stringToNumberFormat);
		ExportSASDataHelper.buildProgram(saasPgm, informat, format, input,
				label, 5);

		return saasPgm.toString();
	}

	/**
	 * Gets the attribute collection.
	 * 
	 * @return the attribute collection
	 */
	private List<AttributeInterface> getAttributeCollection() {
		List<AttributeInterface> attributeCollection = new ArrayList<AttributeInterface>();
		List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList = selectedColumnsMetadata
				.getSelectedAttributeMetaDataList();

		for (QueryOutputTreeAttributeMetadata queryOutputTreeAttributeMetadata : selectedAttributeMetaDataList) {

			AttributeInterface attributeInterface = queryOutputTreeAttributeMetadata
					.getAttribute();
			attributeCollection.add(attributeInterface);
		}
		List<AttributeInterface> clonedAttributeCollection = cloneAttributeCollection(attributeCollection);
		ExportSASDataHelper.removeSpecialCharsFromAttName(clonedAttributeCollection);
		return clonedAttributeCollection;
	}

	/**
	 * @param attributeInterfaCollection
	 * @return
	 */
	private List<AttributeInterface> cloneAttributeCollection(
			List<AttributeInterface> attributeInterfaCollection) {
		List<AttributeInterface> clonedAttributeCollection = new ArrayList<AttributeInterface>();
		for (AttributeInterface attributeInterface : attributeInterfaCollection) {
			AttributeInterface clonedAttributeInterface = new Attribute();
			clonedAttributeInterface
					.setAttributeTypeInformation(attributeInterface
							.getAttributeTypeInformation());
			clonedAttributeInterface.setEntity(attributeInterface.getEntity());
			clonedAttributeInterface.setDescription(attributeInterface
					.getDescription());
			clonedAttributeInterface.setName(attributeInterface.getName());
			clonedAttributeCollection.add(clonedAttributeInterface);
		}
		return clonedAttributeCollection;
	}

}
