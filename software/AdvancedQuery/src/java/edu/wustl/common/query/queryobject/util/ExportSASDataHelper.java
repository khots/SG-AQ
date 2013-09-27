/**
 *
 */

package edu.wustl.common.query.queryobject.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * @author gaurav_mehta
 *
 */
public final class ExportSASDataHelper {

	/**
	 * Instantiates a new export sas data helper.
	 */
	private ExportSASDataHelper() {

	}

	/**
	 * Creates the zip.
	 *
	 * @param sasFileZipEntryName
	 * @param formDataFileZipEntryName
	 *
	 * @return the string
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String createZip(String formDataFileName,
			String saasPGMFileName, String formDataFileZipEntryName,
			String sasFileZipEntryName) throws IOException {
		ZipOutputStream out = null;
		File formFile = null;
		File saasPGMFile = null;
		String zipFileName = null;
		try {
			formFile = new File(formDataFileName);
			saasPGMFile = new File(saasPGMFileName);
			zipFileName = FilenameUtils.getFullPath(formDataFileName)
					+ formFile.getName() + "SAS_IMPORT_PACKAGE.zip";
			File[] selected = { formFile, saasPGMFile };
			String[] zipEntryArray = { formDataFileZipEntryName,
					sasFileZipEntryName };
			byte[] buffer = new byte[1024];
			out = new ZipOutputStream(new FileOutputStream(zipFileName));
			out.setLevel(Deflater.DEFAULT_COMPRESSION);
			for (int i = 0; i < selected.length; i++) {
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = getNewFileInputStream(selected[i]);
					// out.putNextEntry(new ZipEntry(selected[i].getPath()));
					out.putNextEntry(getNewZipEntry(zipEntryArray[i]));
					int len;
					while ((len = fileInputStream.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
				} finally {
					fileInputStream.close();
				}
			}
		} finally {
			out.close();
			formFile.delete();
			saasPGMFile.delete();
		}
		return zipFileName;
	}

	/**
	 * Gets the new zip entry.
	 *
	 * @param name
	 *            the name
	 *
	 * @return the new zip entry
	 */
	private static ZipEntry getNewZipEntry(String name) {
		return new ZipEntry(name);
	}

	/**
	 * Gets the new file input stream.
	 *
	 * @param file
	 *            the file
	 *
	 * @return the new file input stream
	 *
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	private static FileInputStream getNewFileInputStream(File file)
			throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/**
	 * Append import file info.
	 *
	 * @param saasPgm
	 *            the saas pgm
	 */
	public static void appendImportFileInfo(StringBuffer saasPgm,
			String formDataFileName) {
		saasPgm.append("\r\n /*Replace the drive path to reflect where you "
				+ "would like the file stored; */ \r\n\r\n libname CPData"
				+ " '.';\r\n\r\n /* Replace the drive path to reflect "
				+ "where the file is located; */\r\n");

		File formFile = new File(formDataFileName);
		saasPgm.append(new StringBuilder().append("\r\n filename Test '.\\")
				.append(
						formFile.getName().substring(0,
								formFile.getName().indexOf('.'))).append(
						".csv';\r\n").toString());
	}

	/**
	 * Generate file.
	 *
	 * @param saasPgm
	 *            the saas pgm
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String generateFile(String formDataFileName,
			StringBuffer saasPgm) throws IOException {
		BufferedWriter bufferedWriter = null;
		File formFile = new File(formDataFileName);
		String newSaasPGMFileName = FilenameUtils.getFullPath(formDataFileName)
				+ formFile.getName().substring(0,
						formFile.getName().indexOf('.')).replace(' ', '_')
				+ "_SAS_PGM.SAS";
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(
					newSaasPGMFileName));
			bufferedWriter.write(saasPgm.toString());
			bufferedWriter.flush();
		} finally {
			bufferedWriter.close();
		}
		return newSaasPGMFileName;
	}

	/**
	 * @param saasPgm
	 * @param numberToStringFormat
	 * @param stringToNumberFormat
	 */
	public static void appendFormat(StringBuffer saasPgm,
			StringBuffer numberToStringFormat, StringBuffer stringToNumberFormat) {
		saasPgm
				.append("\r\n/********************** numberToStringFormat *******************************"
						+ "***********************/\r\nproc format ; \r\n");
		saasPgm.append(numberToStringFormat);
		saasPgm
				.append("\r\n/********************** stringToNumberFormat *******************************"
						+ "***********************/\r\n");
		saasPgm.append(stringToNumberFormat);
		saasPgm.append("\r\n run;");
	}

	/**
	 * Trim sub form name.
	 *
	 * @param subFormName
	 *            the sub form name
	 *
	 * @return the string
	 */
	public static String trimSubFormName(String subFormName) {
		StringBuffer trimedName = new StringBuffer();
		Pattern pattern = Pattern.compile("[A-Z]");
		for (Matcher matcher = pattern.matcher(subFormName); matcher.find(); trimedName
				.append(matcher.group())) {
		}
		return trimedName.toString();
	}

	/**
	 * Generate format for date attribute.
	 *
	 * @param informat
	 *            the informat
	 * @param format
	 *            the format
	 * @param attributeName
	 *            the attribute name
	 */
	public static void generateFormatForDateAttribute(StringBuffer informat,
			StringBuffer format, String attributeName) {
		informat.append("\r\n informat ");
		informat.append(attributeName);
		informat.append(' ');
		informat.append("mmddyy10.");
		informat.append(" ;");
		format.append("\r\n format ");
		format.append(attributeName);
		format.append(' ');
		format.append("mmddyy10.");
		format.append(" ;");
	}

	/**
	 * Generate format for string attribute.
	 *
	 * @param informat
	 *            the informat
	 * @param format
	 *            the format
	 * @param input
	 *            the input
	 * @param attributeName
	 *            the attribute name
	 * @param size
	 *            the size
	 */
	public static void generateFormatForStringAttribute(StringBuffer informat,
			StringBuffer format, StringBuffer input, String attributeName,
			int size) {
		input.append(" $");
		informat.append("\r\n informat ");
		informat.append(attributeName);
		informat.append(' ');
		informat.append(new StringBuilder().append("$").append(size)
				.append(".").toString());
		informat.append(" ;");
		format.append("\r\n format ");
		format.append(attributeName);
		format.append(' ');
		format.append(new StringBuilder().append("$").append(size).append(".")
				.toString());
		format.append(" ;");
	}

	/**
	 * Process permissible values.
	 *
	 * @param numberToStringFormat
	 *            the number to string format
	 * @param stringToNumberFormat
	 *            the string to number format
	 * @param numberToStringFormatCounter
	 *            the number to string format counter
	 * @param permissibleValue
	 *            the permissible value
	 */
	public static void processPermissibleValues(
			StringBuffer numberToStringFormat,
			StringBuffer stringToNumberFormat, int numberToStringFormatCounter,
			PermissibleValueInterface permissibleValue) {
		String newLine = "\r\n";
		numberToStringFormat.append(newLine);
		numberToStringFormat.append(numberToStringFormatCounter);
		numberToStringFormat.append("  = ");
		Object pvValue = permissibleValue.getValueAsObject();
		if(pvValue!=null)
		{
			pvValue = DynamicExtensionsUtility.getUnEscapedStringValue(pvValue.toString());
		}
		numberToStringFormat.append(new StringBuilder().append(" \"").append(
				pvValue).append(" \"").toString());
		numberToStringFormat.append(newLine);
		stringToNumberFormat.append(newLine);
		stringToNumberFormat.append(new StringBuilder().append("\"").append(
				pvValue).append("\"").toString());
		stringToNumberFormat.append("  = ");
		stringToNumberFormat.append(numberToStringFormatCounter);
		stringToNumberFormat.append(newLine);
	}

	/**
	 * @param input
	 * @return
	 */
	public static String expandString(String input) {
		StringBuffer expandedString = new StringBuffer();
		Pattern pattern = Pattern
				.compile("([a-z][a-z]*)|([A-Z][a-z]*)|([0-9]*)");
		for (Matcher matcher = pattern.matcher(input); matcher.find(); expandedString
				.append(' ').append(matcher.group())) {

		}
		return expandedString.toString();
	}

	/**
	 * Define formats.
	 *
	 * @param numberToStringFormat
	 *            the number to string format
	 * @param stringToNumberFormat
	 *            the string to number format
	 * @param attributeInterface
	 *            the attribute interface
	 */
	public static void defineFormats(StringBuffer numberToStringFormat,
			StringBuffer stringToNumberFormat,
			AttributeInterface attributeInterface) {
		edu.common.dynamicextensions.domaininterface.DataElementInterface dataElement = attributeInterface
				.getAttributeTypeInformation().getDataElement();
		if (dataElement instanceof UserDefinedDEInterface) {
			String attributeName = attributeInterface.getName();
			numberToStringFormat.append(new StringBuilder().append(
					"/* Numeric to character format for ")
					.append(attributeName).append(" */ \r\n").toString());
			stringToNumberFormat.append(new StringBuilder().append(
					"/* Character to numeric format for ")
					.append(attributeName).append(" */ \r\n").toString());
			numberToStringFormat.append(new StringBuilder().append(
					"\r\n value $").append(attributeName).append("noToStrFmt")
					.toString());
			stringToNumberFormat.append(new StringBuilder().append(
					"\r\n value $").append(attributeName).append("strToNoFmat")
					.toString());
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
			Collection<PermissibleValueInterface> permissibleValueCollection = userDefinedDEInterface
					.getPermissibleValueCollection();
			if (permissibleValueCollection != null) {
				int numberToStringFormatCounter = 1;
				for (Iterator<PermissibleValueInterface> i = permissibleValueCollection
						.iterator(); i.hasNext();) {
					PermissibleValueInterface permissibleValue = i.next();
					ExportSASDataHelper.processPermissibleValues(
							numberToStringFormat, stringToNumberFormat,
							numberToStringFormatCounter, permissibleValue);
					numberToStringFormatCounter++;
				}

			}
			numberToStringFormat.append(";\r\n");
			stringToNumberFormat.append(";\r\n");
		}
	}

	/**
	 * Generate format.
	 *
	 * @param informat
	 *            the informat
	 * @param format
	 *            the format
	 * @param input
	 *            the input
	 * @param attributeInterface
	 *            the attribute interface
	 */
	public static void generateFormat(StringBuffer informat,
			StringBuffer format, StringBuffer input, StringBuffer label,
			AttributeInterface attributeInterface, String columnLable) {
		String attributeName = attributeInterface.getName();
		input.append("\r\n ");
		input.append(attributeName);

		label.append("\r\n  label " + attributeName + " = '" + columnLable
				+ "';");

		AttributeTypeInformationInterface attributeTypeInformation = attributeInterface
				.getAttributeTypeInformation();
		if (attributeTypeInformation instanceof StringAttributeTypeInformation) {
			int size = 1000;
			if (((StringAttributeTypeInformation) attributeTypeInformation)
					.getSize() != null) {
				size = ((StringAttributeTypeInformation) attributeTypeInformation)
						.getSize().intValue();
			}
			ExportSASDataHelper.generateFormatForStringAttribute(informat,
					format, input, attributeName, size);
		}
		if (attributeTypeInformation instanceof DateAttributeTypeInformation) {
			ExportSASDataHelper.generateFormatForDateAttribute(informat,
					format, attributeName);
		}
	}

	/**
	 * @param saasPgm
	 * @param informat
	 * @param format
	 * @param input
	 * @param label
	 * @param firstObservation
	 */
	public static void buildProgram(StringBuffer saasPgm,
			StringBuffer informat, StringBuffer format, StringBuffer input,
			StringBuffer label, int firstObservation) {
		saasPgm
				.append("\r\n DATA CPData.RESULTS ; \r\n %let _EFIERR_ = 0; \r\n infile Test dlm=',' MISSOVER DSD lrecl = 32000 firstob"
						+ "s = " + firstObservation + " ; ");
		saasPgm
				.append("\r\n/********************** informat *******************************************"
						+ "***********/\r\n");
		saasPgm.append(informat);
		saasPgm
				.append("\r\n/********************** format *********************************************"
						+ "*********/\r\n");
		saasPgm.append(format);
		saasPgm
				.append("\r\n/********************** label *********************************************"
						+ "*********/\r\n");
		saasPgm.append(label);

		saasPgm
				.append("\r\n/********************** input **********************************************"
						+ "********/\r\n\r\n input ");
		saasPgm.append(input);
		saasPgm
				.append("\r\n ;\r\n if _ERROR_ then call symput('_EFIERR_',1); \r\n  run ;\r\n  proc contents data = cpdata.results; \r\n run; ");
	}

	/**
	 * @param zipFileName
	 * @param sas_fileName
	 * @param sasFileZipEntryName
	 * @throws IOException
	 */
	public static void addSasFileToZip(String zipFileName, String sasFileName,
			String sasFileZipEntryName) throws IOException {

		String dataFileName = sasFileName + "data.csv";
		String dataFileZipEntryName = extractZip(zipFileName, sasFileName,
				dataFileName);
		File sasFile = null;
		File dataFile = null;
		ZipOutputStream out = null;
		FileInputStream sasInputStream = null;
		FileInputStream dataInputStream = null;
		int len;
		byte[] buffer = new byte[1024];
		try {

			out = new ZipOutputStream(new FileOutputStream(zipFileName));
			out.setLevel(Deflater.DEFAULT_COMPRESSION);

			sasFile = new File(sasFileName);
			sasInputStream = addFileContents(sasFileZipEntryName, sasFile, out,
					buffer);

			dataFile = new File(dataFileName);
			dataInputStream = addFileContents(dataFileZipEntryName, dataFile,
					out, buffer);

		} finally {
			sasInputStream.close();
			dataInputStream.close();
			out.close();
			sasFile.delete();
			dataFile.delete();
		}
	}

	/**
	 * @param sasFileZipEntryName
	 * @param sasFile
	 * @param out
	 * @param buffer
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static FileInputStream addFileContents(String sasFileZipEntryName,
			File sasFile, ZipOutputStream out, byte[] buffer)
			throws FileNotFoundException, IOException {
		FileInputStream sasInputStream;
		int len;
		sasInputStream = getNewFileInputStream(sasFile);
		out.putNextEntry(getNewZipEntry(sasFileZipEntryName));
		while ((len = sasInputStream.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		out.closeEntry();
		return sasInputStream;
	}

	/**
	 * @param zipFileName
	 * @param sasFileName
	 * @param dataFileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws IOException
	 */
	private static String extractZip(String zipFileName, String sasFileName,
			String dataFileName) throws IOException {
		ZipEntry zipEntry = null;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;

		try {
			FileInputStream zipFileInputStream = new FileInputStream(
					zipFileName);
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(zipFileInputStream));
			zipEntry = zipInputStream.getNextEntry();

			byte data[] = new byte[1024];
			fileOutputStream = new FileOutputStream(dataFileName);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
			int count = 0;
			while ((count = zipInputStream.read(data, 0, 1024)) != -1) {
				bufferedOutputStream.write(data, 0, count);
			}
		} finally {
			bufferedOutputStream.close();
			fileOutputStream.close();
		}

		return zipEntry.getName();
	}

	/**
	 * @param attributeCollection
	 */
	public static void removeSpecialCharsFromAttName(
			List<AttributeInterface> attributeCollection) {
		for (AttributeInterface attributeInterface : attributeCollection) {
			String attributeName = attributeInterface.getName();
			attributeName = attributeName.replace('(', '_');
			attributeName = attributeName.replace(')', '_');
			attributeName = attributeName.replace('~', '_');
			attributeInterface.setName(attributeName);
		}
	}

}
