#!/usr/bin/env python3

import os
import re
import subprocess

def migrate_service(service_name):
    """Migrate a service from javax to jakarta imports"""
    print(f"\n🔄 Migrating {service_name}...")
    
    # Check if service directory exists (look in parent directory)
    service_path = f"../{service_name}"
    if not os.path.exists(service_path):
        # Try current directory
        service_path = service_name
        if not os.path.exists(service_path):
            print(f"❌ Service directory {service_name} not found")
            return False
    
    # Find all Java files
    java_files = []
    for root, dirs, files in os.walk(f"{service_path}/src"):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    print(f"📁 Found {len(java_files)} Java files")
    
    # Migration mappings
    migrations = {
        # Validation
        'javax.validation': 'jakarta.validation',
        'javax.validation.constraints': 'jakarta.validation.constraints',
        
        # Persistence (JPA)
        'javax.persistence': 'jakarta.persistence',
        
        # Transaction
        'javax.transaction': 'jakarta.transaction',
        
        # Servlet
        'javax.servlet': 'jakarta.servlet',
        'javax.servlet.http': 'jakarta.servlet.http',
        
        # Annotation
        'javax.annotation': 'jakarta.annotation',
    }
    
    total_changes = 0
    
    # Process each Java file
    for java_file in java_files:
        try:
            with open(java_file, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            file_changes = 0
            
            # Apply all migrations
            for old_import, new_import in migrations.items():
                old_pattern = f'import {old_import}'
                new_pattern = f'import {new_import}'
                
                if old_pattern in content:
                    content = content.replace(old_pattern, new_pattern)
                    file_changes += content.count(new_pattern) - original_content.count(new_pattern)
            
            # Write back if changes were made
            if content != original_content:
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                
                relative_path = java_file.replace(f"{service_path}/", "")
                print(f"✅ Updated {relative_path}")
                total_changes += 1
        
        except Exception as e:
            print(f"❌ Error processing {java_file}: {e}")
    
    print(f"✅ Migration completed for {service_name}: {total_changes} files updated")
    return True

def test_compilation(service_name):
    """Test if the service compiles after migration"""
    print(f"\n🧪 Testing compilation for {service_name}...")
    
    try:
        # Test compilation
        result = subprocess.run(
            ['mvn', '-pl', service_name, 'compile', '-q', 
             '-Dmaven.compiler.source=21', '-Dmaven.compiler.target=21'],
            cwd='.',
            capture_output=True,
            text=True,
            timeout=300
        )
        
        if result.returncode == 0:
            print(f"✅ {service_name} compiles successfully!")
            return True
        else:
            print(f"❌ {service_name} compilation failed:")
            print(result.stderr[:1000])  # Show first 1000 chars of error
            return False
            
    except subprocess.TimeoutExpired:
        print(f"⏰ {service_name} compilation timed out")
        return False
    except Exception as e:
        print(f"❌ Error testing {service_name}: {e}")
        return False

def main():
    """Migrate all services that need javax to jakarta migration"""
    
    services_to_migrate = [
        'shipping-service',
        'promotion-service',
        'rating-service', 
        'tax-service',
        'search-service',
        'order-service',
        'payment-service',
        'product-service',
        'user-service',
        'inventory-service',
        'favourite-service'
    ]
    
    print("🚀 Starting javax to jakarta migration...")
    print(f"📋 Services to migrate: {', '.join(services_to_migrate)}")
    
    successful_migrations = []
    failed_migrations = []
    
    for service in services_to_migrate:
        print(f"\n{'='*50}")
        
        # Perform migration
        if migrate_service(service):
            # Test compilation
            if test_compilation(service):
                successful_migrations.append(service)
            else:
                failed_migrations.append(service)
        else:
            failed_migrations.append(service)
    
    # Summary
    print(f"\n{'='*50}")
    print("📊 MIGRATION SUMMARY")
    print(f"{'='*50}")
    print(f"✅ Successful: {len(successful_migrations)}")
    for service in successful_migrations:
        print(f"   - {service}")
    
    if failed_migrations:
        print(f"\n❌ Failed: {len(failed_migrations)}")
        for service in failed_migrations:
            print(f"   - {service}")
    
    print(f"\n🎯 Total: {len(successful_migrations)}/{len(services_to_migrate)} services migrated successfully")
    
    if len(successful_migrations) == len(services_to_migrate):
        print("\n🎉 All services migrated successfully!")
        print("Next step: Update GitHub Actions workflow to include all services")
    
    return len(successful_migrations) == len(services_to_migrate)

if __name__ == "__main__":
    main()