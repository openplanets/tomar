//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.03 at 11:11:50 AM MESZ 
//


package eu.scape_project.tool.toolwrapper.data.tool_spec;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.scape_project.pt.tool package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Tool_QNAME = new QName("http://scape-project.eu/tool", "tool");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.scape_project.pt.tool
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link Inputs }
     * 
     */
    public Inputs createInputs() {
        return new Inputs();
    }

    /**
     * Create an instance of {@link Operations }
     * 
     */
    public Operations createOperations() {
        return new Operations();
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link InOutAttrs }
     * 
     */
    public InOutAttrs createInOutAttrs() {
        return new InOutAttrs();
    }

    /**
     * Create an instance of {@link Operation }
     * 
     */
    public Operation createOperation() {
        return new Operation();
    }

    /**
     * Create an instance of {@link Tool }
     * 
     */
    public Tool createTool() {
        return new Tool();
    }

    /**
     * Create an instance of {@link Stdin }
     * 
     */
    public Stdin createStdin() {
        return new Stdin();
    }

    /**
     * Create an instance of {@link Outputs }
     * 
     */
    public Outputs createOutputs() {
        return new Outputs();
    }

    /**
     * Create an instance of {@link Output }
     * 
     */
    public Output createOutput() {
        return new Output();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Installation }
     * 
     */
    public Installation createInstallation() {
        return new Installation();
    }

    /**
     * Create an instance of {@link OperatingSystemDependency }
     * 
     */
    public OperatingSystemDependency createOperatingSystemDependency() {
        return new OperatingSystemDependency();
    }

    /**
     * Create an instance of {@link License }
     * 
     */
    public License createLicense() {
        return new License();
    }

    /**
     * Create an instance of {@link Format }
     * 
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link Stdout }
     * 
     */
    public Stdout createStdout() {
        return new Stdout();
    }

    /**
     * Create an instance of {@link InOut }
     * 
     */
    public InOut createInOut() {
        return new InOut();
    }

    /**
     * Create an instance of {@link Input }
     * 
     */
    public Input createInput() {
        return new Input();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Tool }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scape-project.eu/tool", name = "tool")
    public JAXBElement<Tool> createTool(Tool value) {
        return new JAXBElement<Tool>(_Tool_QNAME, Tool.class, null, value);
    }

}