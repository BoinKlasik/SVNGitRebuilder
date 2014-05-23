####Introduction

This Java project attempts to permanantly fix issues that are caused by a svn -> git migration that may have occured in the past of a repository.  Generally doing an import from svn to git is fairly painless but there are a few edge cases that cause git to behave oddly when dealing with the files.  Specifically problems occur when you discard hunk/line of a modified file that has EVER been in an svn repository.  After performing a discard hunk/line the entire file will show up as modified to git as long as you have core.autocrlf=true enabled.  The problem is when importing from svn the git svn clone command will ignore the core.autocrlf flag and will commit CR/LF's directly to the git repository.  This causes bit to behave oddly when discarding hunks while the client (rightly so) also has core.autocrlf on.  The problem will persist even if later on down the line a commit is made that makes the file in the repo use only LF's (regardless of the comitter's autocrlf setting)

While a patch should be made to git svn clone (a future project hopefully) this does not solve the problem of already affected repositories.  What this project does is extract the entire affected repository to a new location while changing every blob file that is not binary to simply have LF line endings for the entire history of the repository.

####Things of Note:

1) This is designed to work on a repo that was setup to transition from SVN to git, not while both are still in use.  Most repository settings are destroyed in this transfer and a lot of the svn metadata is lost.

2) Things lost include git reflog, as well as any potentially un garbage collected data (gc isnt very agressive until pruning duration expires, however this tool can only find commits in the git tree)

3) This tool effectively acts as an extremly agressive history rewrite so basically every single commit will have new hashes and will need to be force pushed to origin (if applicable)

4) Branches only tracked by origin will be rewritten as oldRemotes/<oldbranchname> since the new repository will not have any remotes setup they are all converted to local branches.

5) git regularly "packs" the repository in a compressed file located in .git/objects/pack. For this tool to work correctly all pack files must be MOVED (not copied out of the .git folder (but still in the repo, im not sure they work outside of there) and should be extracted with 'git unpack-objects < packfile'. This cuases the pack to be decompressed to its individual files in .git/objects.  If this is not performed, the tool will crash with an error mentioning that it could not find a specific hash file.
