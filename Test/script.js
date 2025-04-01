document.addEventListener('DOMContentLoaded', function() {
    const contentDisplay = document.getElementById('content-display');
    const editor = document.getElementById('editor');
    const boldBtn = document.getElementById('bold-btn');
    const italicBtn = document.getElementById('italic-btn');
    const lineBreakBtn = document.getElementById('line-break-btn');
    const saveBtn = document.getElementById('save-btn');
    const formattedOutput = document.getElementById('formatted-output');
    
    // Load and display content from test.txt
    loadContent();
    
    // Editor buttons functionality
    boldBtn.addEventListener('click', function() {
        document.execCommand('bold', false, null);
        editor.focus();
    });

    italicBtn.addEventListener('click', function() {
        document.execCommand('italic', false, null);
        editor.focus();
    });
    
    lineBreakBtn.addEventListener('click', function() {
        document.execCommand('insertHTML', false, '<br>');
        editor.focus();
    });
    
    // Save content
    saveBtn.addEventListener('click', function() {
        const formattedContent = formatContentForSaving(editor.innerHTML);
        formattedOutput.value = formattedContent;
        
        // In a real application, you would send this to the server
        // For demo purposes, we'll just display the formatted text
        alert('Nội dung đã được định dạng! Kiểm tra ở phần "Kết quả định dạng"');
        
        // Optional: Download as a text file
        downloadTextFile(formattedContent, 'content.txt');
    });
    
    // Load content from the test.txt file
    async function loadContent() {
        try {
            const response = await fetch('test.txt');
            const text = await response.text();
            
            // Display the formatted content
            contentDisplay.innerHTML = formatContentForDisplay(text);
        } catch (error) {
            console.error('Error loading content:', error);
            contentDisplay.innerHTML = '<p style="color: red;">Không thể tải nội dung. Vui lòng kiểm tra file test.txt.</p>';
        }
    }
    
    // Format text from database format to HTML display
    function formatContentForDisplay(text) {
        // Replace \n with <br> for line breaks
        let formattedText = text.replace(/\\n/g, '<br>');
        
        // Process bold and italic formatting in a specific order to avoid conflicts
        
        // 1. Replace text between **** with bold tags
        formattedText = formattedText.replace(/\*\*\*\*(.*?)\*\*\*\*/g, '<strong>$1</strong>');
        
        // 2. Replace text between ** with bold tags
        formattedText = formattedText.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        
        // 3. Replace text between _ _ with italic tags
        formattedText = formattedText.replace(/_(.*?)_/g, '<em>$1</em>');
        
        // 4. Replace text between single * * with italic tags (after handling ** for bold)
        formattedText = formattedText.replace(/\*(.*?)\*/g, '<em>$1</em>');
        
        return formattedText;
    }
    
    // Format HTML content from editor to database format
    function formatContentForSaving(html) {
        // Create a temporary div to work with the HTML
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = html;
        
        // Replace <br> and <div> (new lines in contenteditable) with \n
        let text = tempDiv.innerHTML
            .replace(/<br\s*\/?>/gi, '\\n')
            .replace(/<div\s*\/?>/gi, '\\n')
            .replace(/<\/div>/gi, '');
        
        // Replace <i> and <em> with *
        text = text.replace(/<(i|em)>(.*?)<\/(i|em)>/gi, function(match, p1, p2) {
            return '*' + p2 + '*';
        });
        
        // Replace <b> and <strong> with ** (after handling italics to avoid conflicts)
        text = text.replace(/<(b|strong)>(.*?)<\/(b|strong)>/gi, function(match, p1, p2) {
            return '**' + p2 + '**';
        });
        
        // Clean up any leftover HTML tags
        text = text
            .replace(/<[^>]*>/g, '')
            .replace(/&nbsp;/g, ' ')
            .replace(/\\n\\n/g, '\\n') // Fix double line breaks
            .trim();
        
        return text;
    }
    
    // Function to download text as a file
    function downloadTextFile(content, filename) {
        const blob = new Blob([content], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);
    }
}); 