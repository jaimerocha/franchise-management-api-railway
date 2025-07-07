#!/bin/bash
# prepare-git-commits.sh - Script to add and commit all changes

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🚀 Preparing to commit changes for Franchise Management API${NC}"
echo "=========================================================="
echo ""

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo -e "${RED}❌ Error: Not in a git repository${NC}"
    echo "   Please run this from the project root"
    exit 1
fi

# 1. Show current status
echo -e "${YELLOW}1️⃣ Current Git status:${NC}"
git status --short

# Check if there are changes to commit
if [ -z "$(git status --porcelain)" ]; then
    echo -e "${GREEN}✅ No changes to commit. Repository is up to date!${NC}"
    exit 0
fi

# 2. Add all files
echo -e "\n${YELLOW}2️⃣ Adding all files...${NC}"
git add .

# 3. Show what will be committed
echo -e "\n${YELLOW}3️⃣ Files staged for commit:${NC}"
git diff --cached --name-status

# 4. Ask for confirmation
echo -e "\n${YELLOW}4️⃣ Ready to commit these changes?${NC}"
read -p "Continue? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ Commit cancelled${NC}"
    git reset
    exit 1
fi

# 5. Create commit
echo -e "\n${YELLOW}5️⃣ Creating commit...${NC}"
git commit -m "feat: add infrastructure, testing scripts and production configuration

- Add Terraform infrastructure as code for AWS deployment
- Add production profile configuration in application.yml
- Add comprehensive API testing script (test-api.sh)
- Add quick start script for easy setup
- Add Docker configuration updates
- Update documentation with deployment instructions
- All documentation and code comments in English"

# 6. Show the result
echo -e "\n${GREEN}✅ Changes committed successfully!${NC}"
echo ""
echo -e "${YELLOW}Latest commits:${NC}"
git log --oneline -3

echo ""
echo -e "${YELLOW}Next step:${NC}"
echo -e "Push to GitHub with: ${BLUE}git push origin main${NC}"
echo ""
echo "Your repository: https://github.com/jaimerocha/franchise-management-api"