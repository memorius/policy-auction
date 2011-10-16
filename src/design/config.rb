### 
# Compass
###

# Susy grids in Compass
# First: gem install compass-susy-plugin
# require 'susy'

require 'ninesixty'

# Change Compass configuration
# compass_config do |config|
#   config.output_style = :compact
# end

# compass_config do |config|
#   config.output_style = :nested
#   config.sass_options = { :line_comments => false, :debug_info => true }
# end

###
# Haml
###

# CodeRay syntax highlighting in Haml
# First: gem install haml-coderay
# require 'haml-coderay'

# CoffeeScript filters in Haml
# First: gem install coffee-filter
# require 'coffee-filter'

# Automatic image dimensions on image_tag helper
# activate :automatic_image_sizes

###
# Page command
###

# Per-page layout changes:
# 
# With no layout
# page "/path/to/file.html", :layout => false
# 
# With alternative layout
# page "/path/to/file.html", :layout => :otherlayout
# 
# A path which all have the same layout
# with_layout :admin do
#   page "/admin/*"
# end

# Proxy (fake) files
# page "/this-page-has-no-template.html", :proxy => "/template-file.html" do
#   @which_fake_page = "Rendering a fake page with a variable"
# end

###
# Helpers
###

# Methods defined in the helpers block are available in templates
# helpers do
#   def some_helper
#     "Helping"
#   end
# end

# Change the CSS directory
# set :css_dir, "css"

# Change the JS directory
# set :js_dir, "js"

# Change the images directory
# set :images_dir, "alternative_image_directory"

# Build-specific configuration
configure :build do
  set :slim, :pretty => true 
  activate :minify_css
  activate :minify_javascript
  activate :relative_assets
end


# Pages

data.articles.each do |article|
  page "/articles/#{article.id}.html",  :proxy => "article.tpl.html" do
    @article = article
  end  
end

data.users.each do |user|
  page "/users/#{user.id}.html", :proxy => "user.tpl.html" do
    @user = user
  end
end

data.tags.each do |tag|
  url = tag.title.to_s.downcase.gsub(' ', '-')
  page "/tags/#{url}.html", :proxy => "tag.tpl.html" do
    @tag = tag
  end
end
   