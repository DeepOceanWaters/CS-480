import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.CompilerConfiguration

def importCustomizer = new ImportCustomizer()
importCustomizer.addImport 'SimpleRegex', 'sRegex.classes.SimpleRegex'

def configuration = new CompilerConfiguration()
configuration.addCompilationCustomizers(importCustomizer)

def shell = new GroovyShell(configuration)
shell.evaluate new File('stutest.groovy')