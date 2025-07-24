#!/usr/bin/env python3
import os
import re
from xml.etree import ElementTree as ET

def fix_maven_pom(pom_path):
    """Fix Maven POM files to remove duplicate dependencies and ensure proper inheritance"""
    print(f"Processing {pom_path}")
    
    try:
        tree = ET.parse(pom_path)
        root = tree.getroot()
        
        # Define namespace
        ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
        
        # Find dependencies section
        dependencies = root.find('.//maven:dependencies', ns)
        if dependencies is None:
            print(f"  No dependencies section found in {pom_path}")
            return
        
        # Find all dd-trace-api dependencies
        dd_deps = dependencies.findall(".//maven:dependency[maven:groupId='com.datadoghq'][maven:artifactId='dd-trace-api']", ns)
        
        if len(dd_deps) == 0:
            print(f"  No dd-trace-api dependency found")
            return
        
        if len(dd_deps) == 1:
            # Check if it has a version (it shouldn't if inheriting from parent)
            version_elem = dd_deps[0].find('maven:version', ns)
            if version_elem is not None:
                print(f"  Removing version from dd-trace-api dependency")
                dd_deps[0].remove(version_elem)
            else:
                print(f"  dd-trace-api dependency is already correct")
        else:
            print(f"  Found {len(dd_deps)} duplicate dd-trace-api dependencies, removing duplicates")
            # Keep only the first one and remove version if present
            for i in range(1, len(dd_deps)):
                dependencies.remove(dd_deps[i])
            
            # Remove version from the remaining one
            version_elem = dd_deps[0].find('maven:version', ns)
            if version_elem is not None:
                dd_deps[0].remove(version_elem)
        
        # Write back the file
        tree.write(pom_path, encoding='utf-8', xml_declaration=True)
        print(f"  ✅ Fixed {pom_path}")
        
    except Exception as e:
        print(f"  ❌ Error processing {pom_path}: {e}")

def fix_pom_formatting(pom_path):
    """Fix XML formatting issues"""
    try:
        with open(pom_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Fix namespace declaration if missing
        if 'xmlns="http://maven.apache.org/POM/4.0.0"' not in content:
            content = content.replace('<project', '<project xmlns="http://maven.apache.org/POM/4.0.0"', 1)
        
        # Clean up any malformed XML
        content = re.sub(r'<\?xml version="1\.0" encoding="UTF-8"\?>\s*<\?xml[^>]*\?>', '<?xml version="1.0" encoding="UTF-8"?>', content)
        
        with open(pom_path, 'w', encoding='utf-8') as f:
            f.write(content)
            
    except Exception as e:
        print(f"  Error fixing formatting for {pom_path}: {e}")

# List of service directories
services = [
    'discovery-service',
    'api-gateway', 
    'user-service',
    'product-service',
    'order-service',
    'payment-service',
    'inventory-service',
    'notification-service',
    'shipping-service',
    'favourite-service',
    'promotion-service',
    'rating-service',
    'tax-service',
    'search-service',
    'media-service'
]

print("🔧 Fixing Maven POM files for dd-trace-api dependencies...")

for service in services:
    pom_path = f"{service}/pom.xml"
    if os.path.exists(pom_path):
        fix_maven_pom(pom_path)
        fix_pom_formatting(pom_path)
    else:
        print(f"⚠️  POM file not found: {pom_path}")

print("\n✅ Maven POM fix completed!")
print("\n📝 Note: All services should now inherit the dd-trace-api version from the parent POM.")
print("The parent POM defines: dd-trace-api.version=1.38.1")