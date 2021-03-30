package com.jbk.resilience.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlExtractorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlExtractorUtil.class);

    private XmlExtractorUtil() {
        // util class
    }

    /**
     * Usage: extract("<pokebola><pokemon>charmander</pokemon></pokebola>",
     * "/pokebola/pokemon") will return "charmander"
     *
     * @param xml
     * @param expression
     */
    public static String extract(String xml, String expression) {
        Document document = mountDocument(xml);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        return xPathEvaluate(xpath, expression, document);
    }

    /**
     * Expressão de pesquisa: extract("/NFe/infNFe/emit/CNPJ.
     * <p>
     * Esse procedimento foi repartido para não fazer uma montagem de documento num grupo de pesquisa utilizando
     * o mesmo XML para ganho de performance.
     *
     * @param document   O documento recuperado do XML.
     * @param expression Expressão a ser utilizada no documento.
     * @see XmlExtractorUtil#extract(String, String)
     */
    public static String extractus(Document document, String expression) {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        return xPathEvaluate(xpath, expression, document);
    }

    public static Document mountDocument(byte[] xml) {
        InputSource source = new InputSource(new ByteArrayInputStream(xml));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document document = null;
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(source);
        } catch (Exception e) {
            LOGGER.error("Erro ao executar o metodo extract -> Montagem do Documento", e.getMessage());
            LOGGER.trace("Detalhamento do erro: -> Montagem do Documento", e);
        }
        return document;
    }

    public static Document mountDocument(String xml) {
        String normalizedString = xml.trim();
        normalizedString = normalizedString.substring(normalizedString.indexOf('<'));
        InputSource source = new InputSource(new StringReader(normalizedString));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document document = null;
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(source);
        } catch (Exception e) {
            LOGGER.error("Erro ao executar o metodo extract -> Montagem do Documento", e.getMessage());
            LOGGER.trace("Detalhamento do erro: -> Montagem do Documento", e);
        }
        return document;
    }

    public static String xPathEvaluate(XPath xpath, String expression, Document document) {
        try {
            return xpath.evaluate(expression, document);
        } catch (XPathExpressionException e) {
            LOGGER.error("Erro ao executar o metodo extract", e);
            LOGGER.warn("Erro na extração de dados do XML, expressao {}, não encontrada", expression);
            return "";
        }
    }

    public static String xPathEvaluateToNull(XPath xpath, String expression, Document document, int maxsize) {
        String value = xPathEvaluateToNull(xpath, expression, document);
        return StringUtils.left(value, maxsize);
    }

    public static String xPathEvaluateToNull(XPath xpath, String expression, Document document) {
        try {
            return xpath.evaluate(expression, document);
        } catch (XPathExpressionException e) {
            LOGGER.error("Erro na extração de dados do XML, expressao {}, não encontrada", expression, e);
            return null;
        }
    }

    /**
     * Must receive xml in array of bytes
     *
     * @param file
     * @return
     */
    public static InfoXml getInfoXml(byte[] file) {
        String xml = new String(file);
        xml = xml.trim();
        xml = xml.substring(xml.indexOf('<'));
        final Document document = mountDocument(xml);
        InfoXml infoXml = new InfoXml();
        infoXml.setXml(xml);
        String extract = extractus(document, "/nfeProc");
        if (StringUtils.isNotEmpty(extract))
            processNFe(infoXml, xml);

        extract = extractus(document, "/cteProc");
        if (StringUtils.isNotEmpty(extract))
            processCTe(infoXml, xml);

        extract = extractus(document, "/procEventoCTe");
        if (StringUtils.isNotEmpty(extract))
            processEventoCTe(infoXml, xml);

        processResumo(infoXml, xml);

        return infoXml;
    }

    private static void processEventoCTe(InfoXml infoXml, String xml) {
        final Document document = mountDocument(xml);
        String version = extractus(document, "/procEventoCTe/eventoCTe/@versao");
        if (StringUtils.isNotEmpty(version))
            infoXml.setVersaoXml(version);

        String chaveAcesso = extractus(document, "/procEventoCTe/eventoCTe/infEvento/chCTe");
        if (StringUtils.isNotEmpty(chaveAcesso))
            infoXml.setChaveAcesso(chaveAcesso);

        String tpAmb = extractus(document, "/procEventoCTe/eventoCTe/infEvento/tpAmb");
        if (StringUtils.isNotEmpty(tpAmb))
            infoXml.setTipoAmbienteSefaz(TipoAmbienteSefaz.getByCodigo(tpAmb));
    }

    private static boolean isRetEvento(String xml) {
        String retEvento = extract(xml, "/procEventoNFe/retEvento");
        return StringUtils.isNotEmpty(retEvento);
    }

    private static boolean isRetEventoCTe(String xml) {
        String retEvento = extract(xml, "/procEventoCTe/retEventoCTe");
        return StringUtils.isNotEmpty(retEvento);
    }

    private static void processCTe(InfoXml infoXml, String xml) {
        final Document document = mountDocument(xml);
        String version = extractus(document, "/cteProc/@versao");
        if (StringUtils.isNotEmpty(version))
            infoXml.setVersaoXml(version);

        String chaveAcesso = extractus(document, "/cteProc/CTe/infCte/@Id");
        if (StringUtils.isNotEmpty(chaveAcesso))
            infoXml.setChaveAcesso(chaveAcesso.replace("CTe", ""));

        String tpAmb = extractus(document, "/cteProc/CTe/infCte/ide/tpAmb");
        if (StringUtils.isNotEmpty(tpAmb))
            infoXml.setTipoAmbienteSefaz(TipoAmbienteSefaz.getByCodigo(tpAmb));

    }

    private static void processNFe(InfoXml infoXml, String xml) {
        final Document document = mountDocument(xml);
        String version = extractus(document, "/nfeProc/@versao");
        if (StringUtils.isNotEmpty(version))
            infoXml.setVersaoXml(version);

        String chaveAcesso = extractus(document, "/nfeProc/NFe/infNFe/@Id");
        if (StringUtils.isNotEmpty(chaveAcesso))
            infoXml.setChaveAcesso(chaveAcesso.replace("NFe", ""));

        String tpAmb = extractus(document, "/nfeProc/NFe/infNFe/ide/tpAmb");
        if (StringUtils.isNotEmpty(tpAmb))
            infoXml.setTipoAmbienteSefaz(TipoAmbienteSefaz.getByCodigo(tpAmb));
    }

    public static InfoXml processResumo(InfoXml infoXml, String xml) {
        final Document document = mountDocument(xml);
        String extract = extractus(document, "/resNFe");
        if (StringUtils.isNotEmpty(extract))
            processResNFe(infoXml, xml);

        extract = extractus(document, "/resEvento");
        if (StringUtils.isNotEmpty(extract))
            processResEvento(infoXml, xml);
        return infoXml;
    }

    private static void processResEvento(InfoXml infoXml, String xml) {
        String chaveAcesso = extract(xml, "/resEvento/chNFe");
        if (StringUtils.isNotEmpty(chaveAcesso)) {
            infoXml.setChaveAcesso(chaveAcesso);
        }
    }

    private static void processResNFe(InfoXml infoXml, String xml) {
        String chaveAcesso = extract(xml, "/resNFe/chNFe");
        if (StringUtils.isNotEmpty(chaveAcesso)) {
            infoXml.setChaveAcesso(chaveAcesso);
        }
    }

    /**
     * Must receive xml as String and a expression returns a list of String xmls
     * from this node xml
     * =<pokedex><pokebola><pokemon>charmander</pokemon></pokebola></pokedex><pokedex><pokebola><pokemon>ditto</pokemon></pokebola></pokedex>
     * Usage: extractChild(xml, "pokedex") will return String [0] =
     * <pokebola><pokemon>charmander</pokemon></pokebola> - String [1] =
     * <pokebola><pokemon>ditto</pokemon></pokebola>
     *
     * @param expression
     * @return string list
     */
    public static List<String> extractChild(String expression, XPath xpath, Document document) {

        List<String> childXml = new ArrayList<>();
        try {
            XPathExpression expr = xpath.compile("//" + expression);
            NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < list.getLength(); i++) {

                Node detPag = list.item(i);
                childXml.add(innerXml(detPag));
            }
        } catch (XPathExpressionException e) {
            LOGGER.error("Erro ao executar o metodo {} da classe {}", "extractChild", XmlExtractorUtil.class.getName(), e);
        }
        return childXml;
    }

    /**
     * @see #extractChild(String, XPath, Document)
     */
    @Deprecated
    public static List<String> extractChild(String xml, String expression, XPath xpath, Document document) {
        return extractChild(expression, xpath, document);
    }

    public static String innerXml(Node node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        StringBuilder sb = new StringBuilder();
        sb.append(lsSerializer.writeToString(node));
        return sb.toString();
    }
}
